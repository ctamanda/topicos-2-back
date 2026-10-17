package br.unitins.tp2.fincontrol;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

// Gera um par de chaves RSA temporário para os testes assinarem e validarem JWT.
// Assim os testes rodam em qualquer máquina sem versionar chave privada.
public class ChavesJwtTeste implements QuarkusTestResourceLifecycleManager {

    private Path pasta;

    @Override
    public Map<String, String> start() {
        try {
            KeyPairGenerator gerador = KeyPairGenerator.getInstance("RSA");
            gerador.initialize(2048);
            KeyPair par = gerador.generateKeyPair();

            pasta = Files.createTempDirectory("fincontrol-jwt-teste");
            Path privada = pasta.resolve("privateKey.pem");
            Path publica = pasta.resolve("publicKey.pem");
            Files.writeString(privada, pem("PRIVATE KEY", par.getPrivate().getEncoded()));
            Files.writeString(publica, pem("PUBLIC KEY", par.getPublic().getEncoded()));

            return Map.of(
                    "smallrye.jwt.sign.key.location", privada.toUri().toString(),
                    "mp.jwt.verify.publickey.location", publica.toUri().toString());
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível gerar as chaves JWT de teste.", e);
        }
    }

    @Override
    public void stop() {
        try {
            if (pasta != null) {
                try (var arquivos = Files.list(pasta)) {
                    for (Path arquivo : arquivos.toList()) {
                        Files.deleteIfExists(arquivo);
                    }
                }
                Files.deleteIfExists(pasta);
            }
        } catch (Exception ignorado) {
            // pasta temporária: o sistema limpa depois
        }
    }

    private static String pem(String tipo, byte[] conteudo) {
        String base64 = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(conteudo);
        return "-----BEGIN " + tipo + "-----\n" + base64 + "\n-----END " + tipo + "-----\n";
    }
}
