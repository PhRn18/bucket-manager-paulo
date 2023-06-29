# Este projeto tem como objetivo fornecer endpoints REST para realizar operações básicas em buckets S3. Além disso, inclui toda a configuração necessária para enviar notificações para um tópico SNS em operações mais delicadas, como upload, atualização e exclusão de objetos.

O projeto foi desenvolvido utilizando o framework Spring Boot na linguagem de programação Java.

A documentação detalhada dos endpoints pode ser encontrada na URL do Swagger: /swagger-ui.html.

## Endpoints

- Base URL: `/bucket`

1. `GET /details/{bucketName}`
   - Retorna os detalhes da bucket especificada.

2. `GET /download/{bucketName}/{contentDisposition}`
   - Retorna o arquivo na bucket especificada com a disposição que foi especificada (`attachment` ou `inline`).

3. `GET /list`
   - Retorna todas as buckets que o serviço tem permissão para acessar.

4. `GET /url/{bucketName}/{expirationTime}`
   - Retorna uma URL de acesso pública ao arquivo especificado pelo parâmetro de consulta `key`, com um tempo de expiração predefinido em minutos.

5. `DELETE /{bucketName}`
   - Deleta o arquivo na bucket especificada e com a chave fornecida no parâmetro de consulta.

6. `GET /{bucketName}`
   - Retorna todos os itens presentes na bucket.

7. `POST /{bucketName}`
   - Realiza o upload do arquivo para a bucket.
