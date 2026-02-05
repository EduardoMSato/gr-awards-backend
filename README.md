# Golden Raspberry Awards API

API RESTful que fornece informações sobre produtores com maiores e menores intervalos entre prêmios consecutivos do Golden Raspberry Awards.

## Tecnologias

- Java 8
- Spring Boot 2.7.18
- Spring Data JPA
- H2 Database (in-memory)
- Maven 3.6+
- Apache Commons CSV
- Lombok

## Pré-requisitos

- JDK 8 ou superior
- Maven 3.6 ou superior

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/outsera/goldenraspberry/
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Lógica de negócio
│   │   ├── repository/      # Camada de acesso a dados
│   │   ├── model/           # Entidades JPA
│   │   └── dto/             # Data transfer objects
│   └── resources/
│       ├── application.properties
│       └── movielist.csv    # Dados do Golden Raspberry Awards
└── test/
    └── java/com/outsera/goldenraspberry/
        └── controller/      # Testes de integração
```

## Arquivo CSV

**IMPORTANTE:** A aplicação requer um arquivo CSV específico para funcionar:

- **Nome obrigatório:** `movielist.csv`
- **Localização:** `src/main/resources/movielist.csv`
- **Limitação:** A aplicação suporta apenas um arquivo CSV com este nome exato
- **Carregamento:** Os dados são importados automaticamente na inicialização

Se você precisar usar outro arquivo CSV, será necessário:
1. Substituir o arquivo `movielist.csv` existente, ou
2. Renomear seu arquivo para `movielist.csv`

## Como Executar o Projeto

### 1. Navegue até o diretório do projeto

```bash
cd gr-awards-backend
```

### 2. Execute a aplicação

```bash
mvn spring-boot:run
```

A aplicação será iniciada em `http://localhost:8080`

Os dados do CSV são carregados automaticamente no banco H2 durante a inicialização.

### 3. Acesse a API

**Endpoint:** `GET /api/producers/prize-intervals`

**Descrição:** Retorna os produtores com menores e maiores intervalos entre vitórias consecutivas.

**Exemplo usando curl:**

```bash
curl http://localhost:8080/api/producers/prize-intervals
```

**Exemplo no navegador:**

Acesse `http://localhost:8080/api/producers/prize-intervals`

### 4. Formato da Response

```json
{
  "min": [
    {
      "producer": "Joel Silver",
      "interval": 1,
      "previousWin": 1990,
      "followingWin": 1991
    }
  ],
  "max": [
    {
      "producer": "Matthew Vaughn",
      "interval": 13,
      "previousWin": 2002,
      "followingWin": 2015
    }
  ]
}
```

## Como Executar os Testes de Integração

### Executar todos os testes

```bash
mvn test
```

### Executar classe de teste específica

```bash
mvn test -Dtest=ProducerControllerIntegrationTest
```

### Test Coverage

Os testes de integração verificam:
- Response HTTP 200 OK
- Estrutura correta da response (arrays min e max)
- Precisão dos dados comparados ao arquivo CSV
- Validação de campos (não-nulos, intervalos positivos)
- Corretude do cálculo de intervalos

## H2 Console (Opcional)

O console H2 está disponível para inspeção do banco de dados:

1. Acesse `http://localhost:8080/h2-console`
2. Use as seguintes configurações:
   - JDBC URL: `jdbc:h2:mem:testdb`
   - User: `sa`
   - Password: *(deixe vazio)*

## Regras de Negócio

1. Apenas filmes marcados como vencedores (`winner = yes` no CSV) são considerados
2. Produtores com múltiplas vitórias têm intervalos calculados entre vitórias consecutivas
3. Nomes de produtores separados por "and" ou vírgulas são tratados como produtores individuais
4. A API retorna os intervalos mínimo e máximo encontrados entre todos os produtores
5. Se múltiplos produtores compartilham o mesmo intervalo mínimo/máximo, todos são incluídos na resposta

## Formato dos Dados CSV

A aplicação espera um arquivo CSV (`movielist.csv`) com as seguintes colunas:
- `year` - Ano de lançamento (inteiro)
- `title` - Título do filme (string)
- `studios` - Nomes dos estúdios (string)
- `producers` - Nomes dos produtores separados por "and" ou vírgulas (string)
- `winner` - Indicador de vencedor: "yes" para vencedores, vazio para indicados (string)

**Delimitador:** ponto e vírgula (`;`)

**Exemplo:**
```
year;title;studios;producers;winner
1980;Can't Stop the Music;Associated Film Distribution;Allan Carr;yes
```

## Build

### Compilar apenas

```bash
mvn clean compile
```

### Package (gerar JAR)

```bash
mvn clean package
```

O arquivo JAR será gerado em `target/golden-raspberry-api-1.0.0.jar`

### Executar o JAR gerado

```bash
java -jar target/golden-raspberry-api-1.0.0.jar
```

## Observações

- O banco de dados é recriado a cada reinicialização da aplicação (H2 in-memory)
- Todos os dados do CSV são importados automaticamente na inicialização
- A aplicação utiliza injeção de dependência por construtor
- Logging está habilitado para debug (pode ser ajustado em `application.properties`)
