# I9Midia Software

![Vaadin](https://img.shields.io/badge/Vaadin-00B4F0?style=for-the-badge&logo=Vaadin&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Css](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Eclipse](https://img.shields.io/badge/Eclipse-2C2255?style=for-the-badge&logo=eclipse&logoColor=white)
![Windows](https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white)
![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white)

Aplicação Vaadin + Spring Boot desenvolvida para a I9Media, contendo toda a estrutura necessária para gerenciar processos internos e interfaces de usuário modernas. Este projeto serve como ponto de partida para criar funcionalidades específicas da empresa, com configuração pronta para front-end Vaadin e back-end Spring Boot.

---

## 🔍 Descrição

Este repositório contém o esqueleto de um sistema web corporativo, construído com:

- **Vaadin 14+**: framework para front-end Java (componentes web prontos e responsivos).
- **Spring Boot 2.7+**: para configurar rapidamente o back-end e gerenciar dependências.
- **Maven Wrapper** (`mvnw`): garante que todos usem a mesma versão do Maven, sem precisar instalar globalmente.
- **Estrutura de diretórios** organizada para separar views Java (server-side) dos assets JavaScript/CSS (client-side).

> ⚡ **Importante**: nesta base, você encontrará exemplos de layout, navegação e temas personalizados. Use-a para adicionar telas, serviços e integrações de acordo com a necessidade da I9Media.

---

## 🚀 Tecnologias e Dependências Principais

- **Java 11+** (recomendado)
- **Maven 3.6+** (usado via `./mvnw`)
- **Vaadin Flow 14+** (ou versão configurada no `pom.xml`)
- **Spring Boot Starter Web** (para configuração de servidor e injeção de dependência)
- **Spring Security** (opcional — habilitar se forem necessários autenticação/autorização)
- **Banco de Dados** (PostgreSQL)

---

## 📦 Pré-requisitos

1. **JDK 11 ou superior** instalado e configurado no `PATH`.
2. **Git** (para clonar este repositório).
3. **Maven Wrapper** já incluído; não é necessário ter Maven instalado globalmente, mas se tiver, pode usar `mvn` no lugar de `./mvnw`.
4. (Opcional) **IDE** que suporte Vaadin + Spring (IntelliJ IDEA Community, Eclipse, VS Code com extensão Java).

---

## ⚙️ Como executar localmente

1. **Clone este repositório**  
   ```bash
   git clone https://github.com/LucasFontesB/i9media-software.git
   cd i9media-software
Configurar variáveis de ambiente ou application.properties

Abra src/main/resources/application.properties e ajuste, se necessário.

properties
Copiar
Editar

bash
Copiar
Editar
## No Linux/macOS
./mvnw clean install
./mvnw spring-boot:run

## No Windows (CMD)
mvnw.cmd clean install
mvnw.cmd spring-boot:run
Acesse a aplicação

Front-end Vaadin abrirá automaticamente em http://localhost:8080/ (página padrão definida em Application.java).

Hot reload (Voilá!)

Se usar IntelliJ com o plugin Vaadin/HotswapAgent, execute a classe Application em modo Debug → Debug with HotswapAgent para visualizar alterações Java na hora, sem reiniciar o servidor.

## 🏗 Estrutura do Projeto

Application.java: ponto de entrada Spring Boot; carrega o contexto e disponibiliza o servidor embutido.

views/: cada classe .java representa uma rota (URL) e renderiza componentes Vaadin (Layouts, Grids, Forms etc.).

resources/themes/i9media-theme/: pasta onde ficam os arquivos .css ou .scss para customização visual.

pom.xml: gerencia dependências (Vaadin, Spring Boot Starter Web, JDBC, etc.) e configura plugins (vaadin-maven-plugin, spring-boot-maven-plugin).

## 🔗 Links Úteis
Documentação Vaadin:
https://vaadin.com/docs

Tutorial Vaadin + Spring Boot:
https://vaadin.com/docs/latest/tutorial/overview

Exemplos e demos Vaadin:
https://vaadin.com/examples-and-demos

Cookbook Vaadin (soluções de casos comuns):
https://cookbook.vaadin.com

Components (biblioteca de componentes):
https://vaadin.com/docs/latest/components

Central de extensões (addons):
https://vaadin.com/directory

Stack Overflow (perguntas e respostas Vaadin):
https://stackoverflow.com/questions/tagged/vaadin

Fórum Vaadin:
https://vaadin.com/forum

## 🛠 Fluxo de Desenvolvimento
=== Criar nova View ===

Dentro de src/main/java/com/i9media/views/, crie uma classe Java anotada com @Route("minha-rota") que estenda VerticalLayout ou outro layout Vaadin.

Adicione componentes (ex.: Button, Grid, FormLayout) no construtor da classe.

Caso seja uma dashboard, Registre a rota no NavegadorDashboard.java para aparecer e defina a permissão do usuario ao logar

======

=== Adicionar Dependências ===

Inclua no pom.xml qualquer biblioteca necessária (ex.: spring-boot-starter-data-jpa, driver do banco, etc.).

Execute ./mvnw clean install para baixar e atualizar o classpath.

======

=== Customizar Tema ===

Em src/main/resources/themes/i9media-theme/, edite styles.css (ou styles.scss) para alterar variáveis de cor, fontes ou componentes Vaadin.

No Java, defina @Theme("i9media-theme") na classe MainLayout (ou em Application.java) para ativar o tema.

## 🎯 Objetivos do Projeto para I9Media
Entregar painéis gerenciais: telas de relatórios e dashboards com dados dinâmicos (ex.: faturamento, clientes, comissões).

Automatizar processos internos: CRUDs de agências, veículos de mídia e PIs (Pedidos de Inserção), substituindo planilhas manuais.

Permissões por usuário e departamentos: controle de acesso baseado em papéis (OPEC, Planejamento, Financeiro, ADM/Relatórios, Executivo).

Relatórios exportáveis: gerar relatórios em PDF/Excel para contas a pagar/receber, comissão por vendas, análises por executivo/mês/cliente.

Design responsivo e intuitivo: interface clean com componentes Vaadin, focada em produtividade e facilidade de navegação.

> 📝 Nota: ajuste as rotas e serviços conforme as regras de negócio definidas pela equipe I9Media. Este README deve servir como guia inicial para desenvolvedores que ingressam no projeto.

📫 Contato
Autor: Lucas Fontes Britto

E-mail: lfontesbritto@gmail.com

Desenvolvido com ♥ para a I9Media.
Versão atual: 1.0-SNAPSHOT
© 2025 I9Media.
