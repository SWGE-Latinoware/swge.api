<div style="width:100%; display: flex; flex-direction: row; justify-content: center;">
  <picture>
    <img align="center" src="/metrics.plugin.licenses.svg" alt="Licenças">
  </picture>
  <a target="_blank" href="https://github.com/ta-iot/swge.web">
    <img align="center" src="/metrics.plugin.repositories.svg" alt="Repositórios Vinculados">
  </a>
</div>
 
# __API Sistema Web para Gestão de Eventos__
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![PayPal](https://img.shields.io/badge/PayPal-00457C?style=for-the-badge&logo=paypal&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

Back-end responsável pelas regras de negócio do sistema de gestão de eventos.

## Instalação
Para a utilização da API, é necessário possuir na máquina a qual vai armazenar e executar o projeto certos serviços, que são indicados na seção de Requisitos.

A partir de *clone* (`git clone https://github.com/ta-iot/swge.api.git`), é necessário a execução da instalação dos módulos escolhidos dentro do arquivo `pom.xml`. 

### Requisitos: ###

- Docker;
  - Docker Compose.
- PostgreSQL;
- Ambiente de Desenvolvimento voltado a Java (Eclipse, Intellij, etc..);
- Java 11;
- Maven.

###### Obs: O sistema possui testes de integração do estilo `caixa branca`, e para a execução deste testes, é obrigatório possuir Docker instalado.

### Execução:
Como o projeto utiliza o Spring Boot como *framework* base, a execução do código deve partir do arquivo `Application.java` utilizando o comando Java.
###### Obs: É recomendado o uso de IDEs para a execução do projeto, já que possuem uma praticidade maior.

## __Variáveis de Ambiente__
Dentro da Aplicação, possui diversas variáveis de ambiente que servem para administrar os serviços de e-mail, banco de dados, APIs de pagamento, diretório de arquivos, credenciais de serviços e as implantações em serviços de host de imagens docker.

Para a utilização desta API, é necessário fornecer cada uma destas informações abaixo, utilizando via passagem de paramêtros na execução do sistema. Afim de exemplificar o uso de cada variável, apresentaremos o nome, descrição e a utilização da variável.

### __Variáveis gerais__:
No sistema, temos informações que se remetem a iniciação do projeto, sendo elas:
- __ENV_CYCLE__: Indica o estágio do ciclo de vida do projeto, sendo possível os valores "development" e "production", que indicam os estágios de desenvolvimento e produção do software;
- __FRONTEND_ADDRESS__: Indica a url do site aonde está hosteado o front-end do projeto, sendo utilizado as url de "localhost:porta" quando está sendo feito o desenvolvimento local. Um exemplo é a url "localhost:3000" quando o front-end está sendo desenvolvido em React. Caso seja utilizado outro framework, indique nesta variável a URL local;
- __FILE_DIRECTORY__: Caminho do sistema de arquivos do computador onde está sendo executado o código, é uma alternativa para indicar o local onde será guardado as imagens salvas durante a execução do sistema;
- __USER__: Alternativa para a variável FILE_DIRECTORY, caso não queira especificar um caminho para guardar as imagens, elas serão salvas no caminho "/home/\${USER}/data/system/files". Onde caso queira utilizar um usuário do sistema diferente para armazenar as imagens, insira um usuário valido nesta variável;
- __LGPD_MAIL__: Identifica o e-mail do orgão responsável pela gestão da LGPD do sistema.

### __Variáveis de Banco de Dados__:
O sistema faz uso de um banco de dados PostgreSQL para o armazenamento dos dados cadastrados, e para a inicialização e utilização deste banco, é necessário passar as variáveis:
- __DB_USER__: Identifica o usuário a ser utilizado no banco de dados, certifique-se que este usuário possui permissões do mais alto privilégio. Exemplo: "postgres";
- __DB_PASS__: Senha para realizar o acesso do usuário descrito anteriormente. Exemplo: "swge4321";
- __DB_HOST__: Identifica o local aonde o banco de dados está. Exemplo: "localhost";
- __DB_PORT__: Identifica a porta a qual o banco de dados foi instalado. Exemplo: "5432";
- __DB_NAME__: Identifica o nome do banco de dados criado. Exemplo: "database";
- __DB_SCHEMA__: Identifica o *schema* ou esquema do banco de dados. Exemplo: "public".

### __Variáveis das Credenciais de Serviços Externos__:
Umas das funcionalidades do sistema é a do *Login* Social via os serviços GitHub e Google, onde é coletado informações como nome, e-mail, foto e o id do usuário dentro do serviço. As variáveis para utilizar o *login* social são;
- __GITHUB_CLIENT__: Credencial do *Client ID* da API do GitHub;
- __GITHUB_SECRET__: Credencial do *Client Secret* da API do GitHub;
- __GITHUB_REDIR_URI__: URL a qual a API do GitHub deve redirecionar o usuário após uso do serviço;
- __GOOGLE_CLIENT__: Credencial do *Client ID* da API do Google;
- __GOOGLE_SECRET__: Credencial do *Client Secret* da API do Google;
- __GOOGLE_REDIR_URI__: URL a qual a API do Google deve redirecionar o usuário após uso do serviço.

### __Variáveis do servidor de E-mail__:
Algumas das funcionalidades dentro do sistema, permite avisar o usuário destino com um e-mail. Para a utilização do serviço de e-mail, que é um requisito obrigátorio para a execução do sistema, as variáveis que devem ser passadas são:
- __MAIL_SERVER__: Endereço do *Host* do servidor de e-mail. Um endereço de exemplo para um envio de e-mails pela google é o smtp365.;
- __MAIL_SERVER_PORT__: Porta do servidor de e-mail para a conexão e envio via servidor. O valor padrão é 587 ou 25;
- __MAIL_NOREPLY_SENDER__: E-mail do usuário que irá mandar os e-mails do estilo *No Reply*, ou seja, que não deve ser respondido;
- __MAIL_USER__: E-mail do usuário remetente dos e-mails. O e-mail deve ser condizente ao servidor de e-mail utilizado;
- __MAIL_PASS__: Senha do usuário remetente;
- __MAIL_TEST__: Valor Booleano para permitir que o sistema teste a conexão com o servidor de e-mail, ou não. O valor *false* representa que o sistema não deve testar a conexão, e o valor *true* representa que o sistema deve testar a conexão.

### __Variáveis de APIs de Pagamento__:
Como requisito no sistema, a plataforma faz a gestão de pagamentos de inscrição. Para a realização do pagamento e reembolso de uma inscrição (Cartão de Crédito ou PIX), é utilizado as APIs do PagSeguro e do PayPal. E para fazer a integração com essas APIs, é necessário as credenciais de cada uma, elas são recebidas após a criação da conta na plataforma de cada uma. Já para o PIX, é necessário seguir um [fluxo estipulado](https://dev.pagseguro.uol.com.br/reference/pix-new) pelo PagSeguro para a geração de uma autenticação *secure*

#### __PagSeguro__: 
- __PS_EMAIL__: E-mail da conta PagSeguro utilizada para a integração;
- __PS_PUBLIC_KEY__: Chave publica gerada pela integração, quando em ambiente *sandbox*[^1], ela é obtida fazendo uma requisição *POST* ao PagSeguro. Um guia desta requisição se encontra neste [link](https://dev.pagseguro.uol.com.br/reference/post-public-keys);
- __PS_SECURE_TOKEN__: Credencial do PIX que é gerada durante o tutorial de [autenticação](https://dev.pagseguro.uol.com.br/reference/pix-new);
- __PS_TIMESTAMP__: Valor em milisegundos da Data a qual foi criada o certificado, é explicado a criação e utilização desta variável no tutorial de [autenticação](https://dev.pagseguro.uol.com.br/reference/pix-new);
- __PS_TOKEN__: Credencial vinculado a conta do PagSeguro utilizada, quando em ambiente *sandbox*[^1] a credencial se encontra acessando sua conta no [link](https://sandbox.pagseguro.uol.com.br/vendedor/configuracoes.html). Quando está em ambiente de produção, acessando o [link](https://acesso.pagseguro.uol.com.br/) e seguindo o caminho `Menu Lateral -> Venda Online -> Integrações -> Botão Gerar Token` irá gerar a credencial e será enviado via e-mail;
- __PS_CHARGE_URL__: URL da cobrança de cartão do ambiente usado, sendo o padrão o ambiente *sandbox*[^1] 'https://sandbox.api.pagseguro.com/charges'. Deve-se colocar essa variável caso queira usar o ambiente de produção;
- __PS_SECURE_URL__: URL da cobrança com modo *secure*, que necessita de certificado na requisição, do ambiente usado, sendo o padrão o ambiente *sandbox*[^1] 'https://secure.sandbox.api.pagseguro.com'. Deve-se colocar essa variável caso queira usar o ambiente de produção;
- __PS_PIX__: URL do PIX com o modo *secure*, sendo necessário a disponibilização do certificado digital(arquivos '.jks' e '.p12') dentro da pasta ssl;
- __SSL_PASS__: Senha salva na geração do certificado, utilizado durante a criação do cabeçalho de requisição para os pagamentos;
- __PIX_KEY__: Chave PIX da Instituição ou pessoa a qual receberá os valores da cobranças, em ambiente de produção é necessário essa chave estar cadastrada no PagSeguro;
- __PIX_NAME__: Nome da Instituição ou pessoa a qual receberá os valores da cobranças.

###### obs: As URLs de links e tutoriais do PagSeguro são equivalentes a versão disponibilizada em novembro de 2022. Caso algum link esteja fora do AR é necessário pesquisar a nova URL no site da pagseguro.

#### __PAYPAL__:
Para um entendimento melhor das funcionalidades utilizadas pelo PayPal, a documentação está centralizada [aqui](https://developer.paypal.com/api/rest/). As variáveis que é utilizada no sistema para o pagamento via PayPal são:
- __PP_URL__: URL do serviço de criação de pedidos, sendo feito usando variável de ambiente para realizar a troca do ambiente *sandbox*[^1] para o ambiente de produção[^2];
- __PP_CLIENT_ID__: Credencial gerada no momento da criação da conta do PayPal, utilizada na criação de pedidos, disponibilizada nas informações de integração;
- __PP_SECRET__: Credencial gerada no momento da criação da conta do PayPal, disponibilizada nas informações de integração;

### __Variáveis de Integração e Entrega Contínua(CICD)__:
Caso tenha a necessidade ou curiosidade de adicionar a metodologia DevOps de Integração e Entrega Contínua no projeto utilizando o GitLab ou softwares parecidos, o projeto possui um arquivo configurado de Pipelines (.gitlab-ci.yml) que possui integração com outros softwares de controle de código e versionamento de imagens de containers. As variáveis necessárias para utilizar o CI/CD devem ser adicionados dentro da plataforma do serviço de gerencimento de CI/CD escolhido com os seguintes nomes: 
- __$HB_USER__: Usuário do Serviço Harbor, que faz um host de um repositorio de imagens docker;
- __$HB_PASS__: Senha do usuário do Serviço Harbor;
- __$Server_HOST__: Url do host aonde o Serviço Harbor está hospedado;
- __$SN_URL__: URL do host do Servidor do Sonar, que faz a análise de código para encontrar brechas na aplicação, códigos mal feitos, e a cobertura de testes da aplicação;
- __$SN_TOKEN__: Credencial do host do Serviço Harbor;
- __$IMG_TEST__: URL do diretório de arquivos para armazenar as imagens do projeto quando o ciclo está em desenvolvimento;
- __$IMG_PRO__: URL do diretório de arquivos para armazenar as imagens do projeto quando o ciclo está em produção.

[^1]: Ambiente de teste a qual não reconhece a cobrança como uma cobrança real, não sendo feito a dedução do valor na conta. Para esses ambientes, ambas as APIs fornecem contas ou cartões de teste para a simulação. O PayPal disponibiliza contas de teste que possuem cartões atrelado a conta, é possível saber essas contas atráves deste [link](https://developer.paypal.com/dashboard/accounts), onde é apresentado duas contas de teste, sendo uma o comerciante e outra o cliente(a que deve ser utilizada). Já para o PagSeguro neste [link](https://dev.pagseguro.uol.com.br/reference/testing-cards) possui cartões de teste para a realização de cobranças de teste.
[^2]: Ambiente de uso real, onde todas os valores das cobranças vão ser deduzidas da conta e colocadas na conta de recebimento.

###  Colaboradores  ###
<!-- ALL-CONTRIBUTORS-LIST:START -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://www.linkedin.com/in/marcos-dellazari-7335b483/"><img src="https://media.licdn.com/dms/image/C4E03AQEGpHdT-Qg_EA/profile-displayphoto-shrink_200_200/0/1516893619771?e=1679529600&v=beta&t=T9Q-5XDHrDX4LC89aS5by0j9sn3Xc3bUIAfyXLLFASc" width="100px;" alt="marcos-dellazari-7335b483"  style="border-radius: 50%"/><br /><sub><b>Marcos Dellazari</b></sub></a><br /><span style="font-size: 14px;">PMO Itaipu</span></td>
      <td align="center"><a href="https://www.linkedin.com/in/luiz-felipe-cavalcanti-de-albuquerque-526962156/"><img src="https://media.licdn.com/dms/image/C4D03AQGndz7-V-GIAQ/profile-displayphoto-shrink_200_200/0/1611232704790?e=1680134400&v=beta&t=LUEDJsYVneQ492JaS2IO2YXPShbZr7JEkDcvIspaFgQ" width="100px;" alt="luiz-felipe-cavalcanti-de-albuquerque-526962156"  style="border-radius: 50%"/><br /><sub><b>Luiz Felipe Cavalcanti</b></sub></a><br /><span style="font-size: 14px;">PMO PTI</span></td>
      <td align="center"><a href="https://www.linkedin.com/in/luciano-louren%C3%A7o-452b34a0/"><img src="https://media.licdn.com/dms/image/C4D03AQH0N282vmw-mw/profile-displayphoto-shrink_200_200/0/1563293246742?e=1680134400&v=beta&t=v-l2tiI93iPpixgrze-oBfgJsKbVwkVQIB9iXU9U3No" width="100px;" alt="luciano-louren%C3%A7o-452b34a0"  style="border-radius: 50%"/><br /><sub><b>Luciano Lourenço</b></sub></a><br /><span style="font-size: 14px;">PMO</span></td>
      <td align="center"><a href="https://github.com/RafaelSantosBraz"><img src="https://avatars.githubusercontent.com/u/20260521?v=4?s=100" width="100px;" alt="RafaelSantosBraz" style="border-radius: 50%"/><br /><sub><b>Rafael Braz</b></sub></a><br /><span style="font-size: 14px;">&#127775; Desenvolvedor</span></td>
      <td align="center"><a href="https://github.com/natarafael"><img src="https://avatars.githubusercontent.com/u/52220532?v=4?s=100" width="100px;" alt="natarafael"  style="border-radius: 50%"/><br /><sub><b>Natã Rafael</b></sub></a><br /><span style="font-size: 14px;">Desenvolvedor</span></td>
    </tr>
    <tr>
      <td align="center"><a href="https://github.com/GRSganderla"><img src="https://avatars.githubusercontent.com/u/37743155?v=4?s=100" width="100px;" alt="GRSganderla"  style="border-radius: 50%"/><br /><sub><b>Guilherme R. S.</b></sub></a><br /><span style="font-size: 14px;">Desenvolvedor</span></td>
      <td align="center"><a href="https://github.com/AlannaSantos"><img src="https://avatars.githubusercontent.com/u/86931653?v=4?s=100" width="100px;" alt="AlannaSantos"  style="border-radius: 50%"/><br /><sub><b>Alana Castro dos S.</b></sub></a><br /><span style="font-size: 14px;">Desenvolvedor</span></td>
      <td align="center"><a href="https://github.com/val-ery7793"><img src="https://avatars.githubusercontent.com/u/30504079?v=4?s=100" width="100px;" alt="val-ery7793"  style="border-radius: 50%"/><br /><sub><b>Valéria Nunes</b></sub></a><br /><span style="font-size: 14px;">Gestão de Projeto</span></td>
      <td align="center"><a href="https://www.linkedin.com/in/marcos-andr%C3%A9-f-dos-santos-0aa922232/"><img src="https://media.licdn.com/dms/image/C4E03AQHsoH3emyHBPw/profile-displayphoto-shrink_200_200/0/1654169027338?e=1680134400&v=beta&t=i_S4JrVGPcYHEi_RaM2_a-QYjojC6L5icjJ7h1E7ifU" width="100px;" alt="marcos-andr%C3%A9-f-dos-santos-0aa922232"  style="border-radius: 50%"/><br /><sub><b>Marcos André F</b></sub></a><br /><span style="font-size: 14px;">Gestão de Projeto</span></td>
      <td align="center"><a href="https://www.linkedin.com/in/gilson-dias-de-oliveira-550900220/"><img src="https://avatars.githubusercontent.com/u/108937049?v=4?s=100" width="100px;" alt="GilsonDiasOliveira"  style="border-radius: 50%"/><br /><sub><b>Gilson Dias</b></sub></a><br /><span style="font-size: 14px;">Testes/QA</span></td>
    </tr>
    <tr>
      <td align="center"><a href="https://www.linkedin.com/in/andr%C3%A9-domingos-vasconcelos/"><img src="https://media.licdn.com/dms/image/C5603AQFPYVvwxHuwgA/profile-displayphoto-shrink_200_200/0/1554317112258?e=1679529600&v=beta&t=bI2RHBs8sr67z1GNzA1LCej2oI_-WBJRmit-JxqNrGs" width="100px;" alt="andré-domingos-vasconcelos"  style="border-radius: 50%"/><br /><sub><b>André Domingos</b></sub></a><br /><span style="font-size: 14px;">Design</span></td>
      <td align="center"><a href="https://www.linkedin.com/in/carlos-henrique-leite-217324135/"><img src="https://media.licdn.com/dms/image/C4E03AQHFO2tjH8MwWw/profile-displayphoto-shrink_200_200/0/1540945406159?e=1679529600&v=beta&t=1tmx703CDUdkO8yRyYqbx9WowoB3p1rhjMGKCKAXa7k" width="100px;" alt="carlos-henrique-leite-217324135"  style="border-radius: 50%"/><br /><sub><b>Carlos Henrique</b></sub></a><br /><span style="font-size: 14px;">DevOps</span></td>
      <td align="center"><a href="https://github.com/TheodoroFelipe"><img src="https://avatars.githubusercontent.com/u/109289048?v=4?s=100" width="100px;" alt="TheodoroFelipe"  style="border-radius: 50%"/><br /><sub><b>Felipe Theodoro</b></sub></a><br /><span style="font-size: 14px;">LGPD/Documentação</span></td>
      <td align="center"><a href="https://github.com/SGSNT"><img src="https://avatars.githubusercontent.com/u/122492375?v=4?s=100" width="100px;" alt="SGSNT"  style="border-radius: 50%"/><br /><sub><b>Samuel Dos Santos</b></sub></a><br /><span style="font-size: 14px;">Apoio Documentação</span></td>
      <td align="center"><a href="https://www.linkedin.com/in/mateusschindler"><img src="https://media.licdn.com/dms/image/C4D03AQHuTy6xiXHpag/profile-displayphoto-shrink_200_200/0/1578070070592?e=1679529600&v=beta&t=j6gje_Nj1PcepHD09B6SRVKQtzYUX1CPmIz6-YazwaY" width="100px;" alt="mateusschindler"  style="border-radius: 50%"/><br /><sub><b>Mateus Schindler</b></sub></a><br /><span style="font-size: 14px;">Apoio Documentação</span></td>
    </tr>
    
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

## Parceria
<img src="/.github/logos/PTI-Logo.png" alt="PTI" width="400">

<img src="/.github/logos/IB-Logo.png" alt="IB"  width="400">
