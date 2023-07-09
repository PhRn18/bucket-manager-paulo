# Bucket Controller

O `BucketController` é um controlador REST responsável por lidar com várias funcionalidades relacionadas a um bucket. Ele expõe endpoints para executar operações como listar buckets, listar o conteúdo de um bucket, fazer upload de arquivos, baixar arquivos, buscar arquivos, entre outras operações.

## Endpoints

### Listar todos os buckets

- **URL:** `/bucket/list`
- **Método:** GET
- **Descrição:** Retorna uma lista de todos os buckets.
- **Resposta de sucesso:** Retorna uma lista de objetos `BucketDetails` contendo informações sobre cada bucket.
- **Códigos de status:**
  - 200 (OK): Requisição bem-sucedida.
  - 204 (No Content): Não há conteúdo para exibir.

### Listar conteúdo de um bucket

- **URL:** `/bucket/{bucketName}`
- **Método:** GET
- **Descrição:** Retorna o conteúdo de um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket a ser listado.
- **Resposta de sucesso:** Retorna um objeto `BucketContent` contendo o conteúdo do bucket.
- **Códigos de status:**
  - 200 (OK): Requisição bem-sucedida.

### Listar detalhes do conteúdo de um bucket

- **URL:** `/bucket/details/{bucketName}`
- **Método:** GET
- **Descrição:** Retorna os detalhes do conteúdo de um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket do qual se deseja obter os detalhes.
  - `key` (obrigatório): A chave do conteúdo a ser buscado.
- **Resposta de sucesso:** Retorna um objeto `ContentDetails` contendo os detalhes do conteúdo.
- **Códigos de status:**
  - 200 (OK): Requisição bem-sucedida.

### Fazer upload de um arquivo para um bucket

- **URL:** `/bucket/{bucketName}`
- **Método:** POST
- **Descrição:** Faz o upload de um arquivo para um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket para onde o arquivo será enviado.
- **Parâmetros de formulário:**
  - `file` (obrigatório): O arquivo a ser enviado.
- **Resposta de sucesso:** Retorna um objeto `FileUploaded` contendo informações sobre o arquivo enviado.
- **Códigos de status:**
  - 201 (Created): Requisição bem-sucedida.

### Comprimir e atualizar um arquivo em um bucket

- **URL:** `/bucket/compress/{bucketName}`
- **Método:** POST
- **Descrição:** Comprime e atualiza um arquivo existente em um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket onde o arquivo está localizado.
- **Parâmetros de formulário:**
  - `file` (obrigatório): O arquivo a ser comprimido e atualizado.
- **Resposta de sucesso:** Retorna um objeto `CompressedFileUpdate` contendo informações sobre o arquivo comprimido e atualizado.
- **Códigos de status:**
  - 201 (Created): Requisição bem-sucedida.

### Baixar um arquivo de um bucket

- **URL:** `/bucket/download/{bucketName}/{contentDisposition}`
- **Método:** GET
- **Descrição:** Baixa um arquivo de um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket onde o arquivo está localizado.
  - `key` (obrigatório): A chave do arquivo a ser baixado.
  - `contentDisposition` (opcional): A disposição do conteúdo do arquivo (inline, attachment).
- **Resposta de sucesso:** Retorna o arquivo para download.
- **Códigos de status:**
  - 200 (OK): Requisição bem-sucedida.

### Gerar URL de um arquivo em um bucket

- **URL:** `/bucket/url/{bucketName}/{expirationTime}`
- **Método:** GET
- **Descrição:** Gera uma URL para acesso a um arquivo em um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket onde o arquivo está localizado.
  - `key` (obrigatório): A chave do arquivo para o qual se deseja gerar a URL.
  - `expirationTime` (obrigatório): O tempo de expiração da URL.
- **Resposta de sucesso:** Retorna a URL gerada.
- **Códigos de status:**
  - 200 (OK): Requisição bem-sucedida.

### Buscar arquivos em um bucket

- **URL:** `/bucket/search/{bucketName}`
- **Método:** GET
- **Descrição:** Busca arquivos em um bucket específico com base em uma string de busca.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket onde a busca será realizada.
  - `searchString` (obrigatório): A string de busca.
- **Resposta de sucesso:** Retorna um objeto `SearchFileResult` contendo o resultado da busca.
- **Códigos de status:**
  - 200 (OK): Requisição bem-sucedida.

### Listar todas as pastas de um bucket

- **URL:** `/bucket/folders/{bucketName}`
- **Método:** GET
- **Descrição:** Lista todas as pastas de um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket do qual se deseja listar as pastas.
- **Resposta de sucesso:** Retorna um objeto `ListAllFoldersResult` contendo a lista de todas as pastas.
- **Códigos de status:**
  - 200 (OK): Requisição bem-sucedida.

### Obter o tamanho de todas as pastas de um bucket

- **URL:** `/bucket/folders/listSize/{bucketName}`
- **Método:** GET
- **Descrição:** Obtém o tamanho de todas as pastas de um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket do qual se deseja obter o tamanho das pastas.
- **Resposta de sucesso:** Retorna um objeto `FoldersSize` contendo o tamanho de todas as pastas.
- **Códigos de status:**
  - 200 (OK): Requisição bem-sucedida.

### Listar todas asextensões de arquivos de um bucket

- **URL:** `/bucket/fileExtensions/{bucketName}`
- **Método:** GET
- **Descrição:** Lista todas as extensões de arquivos presentes em um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket do qual se deseja listar as extensões de arquivos.
- **Resposta de sucesso:** Retorna um objeto `ListAllFileExtensions` contendo a lista de todas as extensões de arquivos.
- **Códigos de status:**
  - 200 (OK): Requisição bem-sucedida.

### Contar ocorrências de uma extensão de arquivo em um bucket

- **URL:** `/bucket/fileExtensions/count/{bucketName}/{extension}`
- **Método:** GET
- **Descrição:** Conta o número de ocorrências de uma determinada extensão de arquivo em um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket no qual se deseja contar as ocorrências da extensão de arquivo.
  - `extension` (obrigatório): A extensão de arquivo para a qual se deseja contar as ocorrências.
- **Resposta de sucesso:** Retorna um objeto `CountExtensionOccurrences` contendo o número de ocorrências da extensão de arquivo.
- **Códigos de status:**
  - 200 (OK): Requisição bem-sucedida.

### Mover um arquivo para outro bucket

- **URL:** `/bucket/move/{sourceBucket}/{targetBucket}`
- **Método:** PUT
- **Descrição:** Move um arquivo de um bucket de origem para um bucket de destino.
- **Parâmetros de URL:**
  - `sourceBucket` (obrigatório): O nome do bucket de origem.
  - `targetBucket` (obrigatório): O nome do bucket de destino.
- **Parâmetros de consulta:**
  - `key` (obrigatório): A chave do arquivo a ser movido.
- **Resposta de sucesso:** Retorna uma resposta vazia com o código de status 201 (Created).
- **Códigos de status:**
  - 201 (Created): Requisição bem-sucedida.

### Excluir um arquivo de um bucket

- **URL:** `/bucket/{bucketName}`
- **Método:** DELETE
- **Descrição:** Exclui um arquivo de um bucket específico.
- **Parâmetros de URL:**
  - `bucketName` (obrigatório): O nome do bucket do qual se deseja excluir o arquivo.
- **Parâmetros de consulta:**
  - `key` (obrigatório): A chave do arquivo a ser excluído.
- **Resposta de sucesso:** Retorna uma resposta vazia com o código de status 201 (Created).
- **Códigos de status:**
  - 201 (Created): Requisição bem-sucedida.

