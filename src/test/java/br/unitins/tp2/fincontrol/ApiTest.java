package br.unitins.tp2.fincontrol;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.tp2.fincontrol.model.PerfilFiscal;
import br.unitins.tp2.fincontrol.model.Role;
import br.unitins.tp2.fincontrol.model.Usuario;
import br.unitins.tp2.fincontrol.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

// Testes de ponta a ponta da API (H2 em memória, chaves JWT geradas pelo Quarkus).
@QuarkusTest
@WithTestResource(ChavesJwtTeste.class)
class ApiTest {

    private static final String ADMIN_EMAIL = "admin@teste.local";
    private static final String SENHA = "123456";

    @Inject
    UsuarioRepository usuarioRepository;

    @BeforeEach
    void garantirAdmin() {
        // No perfil de teste o Flyway está desligado, então a V10 (admin) não roda.
        QuarkusTransaction.requiringNew().run(() -> {
            if (usuarioRepository.findByEmail(ADMIN_EMAIL) == null) {
                Usuario admin = new Usuario();
                admin.setEmail(ADMIN_EMAIL);
                admin.setNome("Admin");
                admin.setPassword(BcryptUtil.bcryptHash(SENHA));
                admin.setPerfilFiscal(PerfilFiscal.PF);
                admin.setRole(Role.ADMIN);
                usuarioRepository.persist(admin);
            }
        });
    }

    // ------------------------------------------------------------------ autenticação

    @Test
    void cadastroDevolveJsonComEmailEmMinusculas() {
        String email = novoEmail();
        given().contentType(ContentType.JSON)
                .body(cadastro(email.toUpperCase(), "PF", null))
                .when().post("/auth/register")
                .then().statusCode(201)
                .body("email", equalTo(email))
                .body("role", equalTo("USER"))
                .body("id", notNullValue());
    }

    @Test
    void naoPermiteEmailRepetidoMesmoComMaiusculas() {
        String email = novoEmail();
        registrar(email);
        given().contentType(ContentType.JSON)
                .body(cadastro(email.toUpperCase(), "PF", null))
                .when().post("/auth/register")
                .then().statusCode(400)
                .body("detail", equalTo("E-mail já cadastrado."));
    }

    @Test
    void loginIgnoraMaiusculasNoEmail() {
        String email = novoEmail();
        registrar(email);
        given().contentType(ContentType.JSON)
                .body(Map.of("email", email.toUpperCase(), "password", SENHA))
                .when().post("/auth/login")
                .then().statusCode(200)
                .body("token", notNullValue())
                .body("expiresIn", equalTo(3600));
    }

    @Test
    void senhaErradaDevolve401ComMensagem() {
        String email = novoEmail();
        registrar(email);
        given().contentType(ContentType.JSON)
                .body(Map.of("email", email, "password", "errada1"))
                .when().post("/auth/login")
                .then().statusCode(401)
                .body("detail", equalTo("Credenciais inválidas."));
    }

    @Test
    void meiSemAtividadeDevolve400() {
        given().contentType(ContentType.JSON)
                .body(cadastro(novoEmail(), "MEI", null))
                .when().post("/auth/register")
                .then().statusCode(400);
    }

    @Test
    void cadastroCriaSeisCategoriasIniciais() {
        String token = registrarELogar();
        given().auth().oauth2(token)
                .when().get("/categorias")
                .then().statusCode(200)
                .body("size()", equalTo(6))
                .body("nome", hasItem("Alimentação"));
    }

    // ------------------------------------------------------------------ erros de entrada

    @Test
    void corpoAusenteDevolve400EmVezDe500() {
        given().contentType(ContentType.JSON)
                .when().post("/auth/register")
                .then().statusCode(400)
                .body("errors.field", contains("corpo"));
    }

    @Test
    void jsonMalformadoDevolve400() {
        given().contentType(ContentType.JSON).body("{\"email\": ")
                .when().post("/auth/register")
                .then().statusCode(400)
                .body("errors.field", contains("corpo"));
    }

    @Test
    void enumInvalidoListaValoresAceitos() {
        given().contentType(ContentType.JSON)
                .body(cadastro(novoEmail(), "XYZ", null))
                .when().post("/auth/register")
                .then().statusCode(400)
                .body("errors[0].field", equalTo("perfilFiscal"))
                .body("errors[0].message", equalTo("Valor inválido. Use um destes: PF, MEI, ME."));
    }

    @Test
    void semTokenDevolve401() {
        given().when().get("/transacoes").then().statusCode(401);
    }

    // ------------------------------------------------------------------ transações

    @Test
    void criaTransacaoCom201EListaSoAsProprias() {
        String tokenA = registrarELogar();
        String tokenB = registrarELogar();

        given().auth().oauth2(tokenA).contentType(ContentType.JSON)
                .body(transacao("Mercado", "DESPESA", categoria(tokenA, "DESPESA")))
                .when().post("/transacoes")
                .then().statusCode(201)
                .body("categoria.tipo", equalTo("DESPESA"));

        given().auth().oauth2(tokenA).when().get("/transacoes")
                .then().statusCode(200).body("totalItems", equalTo(1));
        given().auth().oauth2(tokenB).when().get("/transacoes")
                .then().statusCode(200).body("totalItems", equalTo(0));
    }

    @Test
    void outroUsuarioNaoLeEditaNemExcluiTransacao() {
        String tokenA = registrarELogar();
        String tokenB = registrarELogar();
        long id = criarTransacao(tokenA, "DESPESA");

        given().auth().oauth2(tokenB).when().get("/transacoes/" + id).then().statusCode(404);
        given().auth().oauth2(tokenB).contentType(ContentType.JSON)
                .body(transacao("Invasão", "DESPESA", categoria(tokenB, "DESPESA")))
                .when().put("/transacoes/" + id).then().statusCode(404);
        given().auth().oauth2(tokenB).when().delete("/transacoes/" + id).then().statusCode(404);
    }

    @Test
    void naoUsaCategoriaDeOutroUsuario() {
        String tokenA = registrarELogar();
        String tokenB = registrarELogar();

        given().auth().oauth2(tokenA).contentType(ContentType.JSON)
                .body(transacao("Teste", "DESPESA", categoria(tokenB, "DESPESA")))
                .when().post("/transacoes")
                .then().statusCode(400)
                .body("errors[0].field", equalTo("idCategoria"));
    }

    @Test
    void despesaNaoAceitaCategoriaDeReceita() {
        String token = registrarELogar();
        given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(transacao("Teste", "DESPESA", categoria(token, "RECEITA")))
                .when().post("/transacoes")
                .then().statusCode(400)
                .body("errors[0].field", equalTo("idCategoria"));
    }

    @Test
    void validaCamposDaTransacao() {
        String token = registrarELogar();
        given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("descricao", "x", "valor", -5))
                .when().post("/transacoes")
                .then().statusCode(400)
                .body("errors.field", hasItem("valor"))
                .body("errors.field", hasItem("idCategoria"));
    }

    @Test
    void limitaTamanhoDaPagina() {
        String token = registrarELogar();
        given().auth().oauth2(token).queryParam("pageSize", 1000)
                .when().get("/transacoes")
                .then().statusCode(400)
                .body("errors[0].field", equalTo("pageSize"));
    }

    // ------------------------------------------------------------------ categorias

    @Test
    void nomeDeCategoriaRepetidoIgnoraEspacosEMaiusculas() {
        String token = registrarELogar();
        given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("nome", "  LAZER  ", "tipo", "DESPESA"))
                .when().post("/categorias")
                .then().statusCode(400)
                .body("errors[0].field", equalTo("nome"));
    }

    @Test
    void categoriaComTransacoesNaoPodeSerExcluidaNemMudarDeTipo() {
        String token = registrarELogar();
        long idCategoria = categoria(token, "DESPESA");
        given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(transacao("Conta", "DESPESA", idCategoria))
                .when().post("/transacoes").then().statusCode(201);

        given().auth().oauth2(token).when().delete("/categorias/" + idCategoria)
                .then().statusCode(400);
        given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("nome", "Outro nome", "tipo", "RECEITA"))
                .when().put("/categorias/" + idCategoria)
                .then().statusCode(400)
                .body("errors[0].field", equalTo("tipo"));
    }

    @Test
    void outroUsuarioNaoAcessaCategoria() {
        String tokenA = registrarELogar();
        String tokenB = registrarELogar();
        long idCategoria = categoria(tokenA, "DESPESA");
        given().auth().oauth2(tokenB).when().get("/categorias/" + idCategoria).then().statusCode(404);
        given().auth().oauth2(tokenB).when().delete("/categorias/" + idCategoria).then().statusCode(404);
    }

    // ------------------------------------------------------------------ perfil

    @Test
    void atualizaPerfilEDescartaAtividadeForaDoMei() {
        String token = registrarELogar();
        given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("nome", "  Novo Nome  ", "perfilFiscal", "ME", "atividadeMei", "SERVICO"))
                .when().put("/usuarios/me")
                .then().statusCode(200)
                .body("nome", equalTo("Novo Nome"))
                .body("perfilFiscal", equalTo("ME"))
                .body("atividadeMei", equalTo(null));
    }

    // ------------------------------------------------------------------ administração

    @Test
    void usuarioComumNaoAcessaAdmin() {
        String token = registrarELogar();
        given().auth().oauth2(token).when().get("/admin/estatisticas").then().statusCode(403);
        given().auth().oauth2(token).when().get("/admin/parametros").then().statusCode(403);
    }

    @Test
    void adminVeSomenteNumerosAgregados() {
        given().auth().oauth2(logar(ADMIN_EMAIL))
                .when().get("/admin/estatisticas")
                .then().statusCode(200)
                .body("totalAdmins", equalTo(1))
                .body("totalUsuarios", notNullValue());
    }

    @Test
    void parametroFiscalNormalizaChaveENaoAceitaSobreposicao() {
        String admin = logar(ADMIN_EMAIL);
        String sufixo = UUID.randomUUID().toString().substring(0, 6);

        int id = given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(parametro("teto mei " + sufixo, "2030-01-01", "2030-12-31"))
                .when().post("/admin/parametros")
                .then().statusCode(201)
                .body("chave", equalTo(("TETO_MEI_" + sufixo).toUpperCase()))
                .extract().path("id");

        given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(parametro("TETO_MEI_" + sufixo.toUpperCase(), "2030-06-01", "2031-05-31"))
                .when().post("/admin/parametros")
                .then().statusCode(400);

        given().auth().oauth2(admin).contentType(ContentType.JSON)
                .body(parametro("teto mei " + sufixo, "2030-01-01", "2030-12-31"))
                .when().put("/admin/parametros/" + id)
                .then().statusCode(200);

        given().auth().oauth2(admin).when().delete("/admin/parametros/" + id).then().statusCode(204);
    }

    @Test
    void parametroComFimAntesDoInicioDevolve400() {
        given().auth().oauth2(logar(ADMIN_EMAIL)).contentType(ContentType.JSON)
                .body(parametro("QUALQUER", "2030-12-31", "2030-01-01"))
                .when().post("/admin/parametros")
                .then().statusCode(400);
    }

    // ------------------------------------------------------------------ apoio

    private static String novoEmail() {
        return "teste-" + UUID.randomUUID().toString().substring(0, 8) + "@teste.local";
    }

    private static Map<String, Object> cadastro(String email, String perfil, String atividade) {
        var corpo = new java.util.HashMap<String, Object>();
        corpo.put("email", email);
        corpo.put("nome", "Pessoa Teste");
        corpo.put("password", SENHA);
        corpo.put("perfilFiscal", perfil);
        corpo.put("atividadeMei", atividade);
        return corpo;
    }

    private static void registrar(String email) {
        given().contentType(ContentType.JSON).body(cadastro(email, "PF", null))
                .when().post("/auth/register").then().statusCode(201);
    }

    private static String logar(String email) {
        return given().contentType(ContentType.JSON)
                .body(Map.of("email", email, "password", SENHA))
                .when().post("/auth/login")
                .then().statusCode(200)
                .extract().path("token");
    }

    private static String registrarELogar() {
        String email = novoEmail();
        registrar(email);
        return logar(email);
    }

    // Id da primeira categoria do usuário com o tipo pedido.
    private static long categoria(String token, String tipo) {
        List<Map<String, Object>> categorias = given().auth().oauth2(token)
                .when().get("/categorias")
                .then().statusCode(200)
                .extract().jsonPath().getList("$");
        return categorias.stream()
                .filter(c -> tipo.equals(c.get("tipo")))
                .map(c -> ((Number) c.get("id")).longValue())
                .findFirst()
                .orElseThrow();
    }

    private static Map<String, Object> transacao(String descricao, String tipo, long idCategoria) {
        return Map.of(
                "descricao", descricao,
                "valor", 10.50,
                "data", "2026-10-16",
                "tipo", tipo,
                "escopo", "PESSOAL",
                "idCategoria", idCategoria);
    }

    private static long criarTransacao(String token, String tipo) {
        Number id = given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(transacao("Teste", tipo, categoria(token, tipo)))
                .when().post("/transacoes")
                .then().statusCode(201)
                .extract().path("id");
        return id.longValue();
    }

    private static Map<String, Object> parametro(String chave, String inicio, String fim) {
        return Map.of("chave", chave, "valor", 81000.00, "vigenciaInicio", inicio, "vigenciaFim", fim);
    }
}
