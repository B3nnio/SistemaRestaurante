# Sistema de Restaurante

## Sobre o projeto

Este projeto foi desenvolvido como atividade acadêmica pelos alunos Bendto de Lima Neto e Guilherme Martins de Sá sdo curso de Análise e Desenvolvimento de Sistemas (ADS).

O objetivo é criar um sistema de gerenciamento de restaurante utilizando Java e MySQL, permitindo cadastrar clientes, consultar o cardápio e gerenciar pedidos.

O sistema funciona pelo terminal, utilizando um menu interativo para acessar suas funcionalidades.

## Funcionalidades

**Gerenciamento de clientes**
- Cadastrar clientes.
- Listar clientes cadastrados.
- Alterar nome e telefone dos clientes.
- Excluir clientes que não possuem pedidos registrados.

**Gerenciamento de pedidos**
- Cadastrar pedidos para clientes existentes.
- Adicionar pratos e informar suas quantidades.
- Consultar o histórico de pedidos.
- Alterar as quantidades dos pratos.
- Adicionar ou remover pratos de pedidos existentes.
- Excluir pedidos.
- Calcular e atualizar automaticamente o valor total.

**Cardápio**
- Consultar os pratos cadastrados no banco de dados.
- Visualizar nome, descrição, categoria e preço dos pratos.

## Tecnologias utilizadas

- Java
- MySQL
- JDBC (conexão entre Java e MySQL)
- IntelliJ IDEA
- MySQL Workbench
- Git e GitHub

## Banco de dados

O projeto utiliza o banco de dados `restaurante`, composto por quatro tabelas:

- `clientes` — armazena os dados dos clientes.
- `pratos` — armazena os pratos disponíveis no cardápio.
- `pedidos` — armazena os pedidos realizados.
- `itens_pedido` — armazena os pratos e suas quantidades em cada pedido.

Os arquivos SQL estão disponíveis na pasta `database`:

- `restaurante.sql` — cria o banco de dados e suas tabelas.
- `pratos_dados.sql` — insere os pratos iniciais do cardápio.

**Atenção:** o arquivo `restaurante.sql` contém comandos que podem recriar tabelas. Utilize-o em um banco de testes vazio para evitar a perda de dados existentes.

## Como executar o projeto

1. Ter o Java e o MySQL instalados.
2. Abrir o projeto no IntelliJ IDEA.
3. Configurar o driver JDBC do MySQL no projeto.
4. Executar o arquivo `database/restaurante.sql` no MySQL Workbench para criar o banco e as tabelas.
5. Selecionar o banco `restaurante` e executar `database/pratos_dados.sql` para cadastrar os pratos.
6. Configurar a variável de ambiente `MYSQL_PASSWORD` com a senha local do MySQL.
7. Executar a classe `Restaurante.java` para iniciar o sistema.

## Organização do projeto

- `src/` — contém as classes Java.
- `database/` — contém os arquivos SQL utilizados no banco de dados.
- `README.md` — apresenta as informações e instruções do projeto.

## Objetivo acadêmico

O projeto foi desenvolvido para colocar em prática os conhecimentos de programação orientada a objetos, operações CRUD, integração com banco de dados e utilização de comandos SQL.