# processo de assinatura de aplicativos
A assinatura de aplicativos é um processo essencial para garantir a integridade e a autenticidade do software distribuído. Este documento descreve os passos necessários para assinar um aplicativo, seja ele para plataformas móveis ou desktop.

## Passos para Assinar um Aplicativo Android
    1. **Gerar um Keystore**: Utilize o comando `keytool` para criar um keystore que armazenará sua chave privada.
   ```bash
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
   ```
    - sera solicitado que você insira informações como nome, organização e localização.
    1.1 **defina uma senha segura para o keystore e para a chave.**
    - - gerando a senha com o comando a seguir:
   ```bash
    openssl rand -base64 32
   ```
   2. **Configurar o Gradle**: No arquivo `build.gradle`, configure as propriedades de assinatura.
      ```groovy 
        android {
            ...
            signingConfigs {
                release {
                    storeFile file("my-release-key.jks")
                    storePassword "your-store-password"
                    keyAlias "my-alias"
                    keyPassword "your-key-password"
                }
            }
            buildTypes {
                release {
                    signingConfig signingConfigs.release
                }
            }
        }
      ```
      3. **Gerar o APK Assinado**: Execute o comando para gerar o APK assinado.
         ```bash
         ./gradlew assembleRelease
         ```
      - O APK assinado estará disponível na pasta `app/build/outputs/apk/release/`.
      - Após a assinatura, é recomendável verificar a assinatura do APK usando o comando:
         ```bash
         jarsigner -verify -verbose -certs app-release.apk
         ```
### Após assinadado o app esta pronto para ser distribuído na Google Play Store ou outras plataformas.
      