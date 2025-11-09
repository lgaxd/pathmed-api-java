# PathMed API

![Java](https://img.shields.io/badge/Java-17-orange)
![Licença](https://img.shields.io/badge/licença-MIT-green)
![Status](https://img.shields.io/badge/status-desenvolvimento-blue)

Uma API RESTful para gerenciamento de consultas médicas e registros de pacientes do sistema de saúde PathMed.

## 📋 Sumário

- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Endpoints da API](#-endpoints-da-api)
- [Banco de Dados](#-banco-de-dados)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Como Contribuir](#-como-contribuir)

## ✨ Funcionalidades

- Gerenciamento e cadastro de pacientes
- Agendamento de consultas médicas
- Gestão de especialidades médicas
- Gerenciamento de profissionais de saúde
- Autenticação e autorização
- Verificação de disponibilidade de consultas em tempo real
- Acesso a registros médicos dos pacientes

## 🛠 Tecnologias

- Java 17
- Oracle Database
- GSON para processamento de JSON
- Servidor HTTP nativo do Java
- Driver OracleJDBC
- Bruno para testes de requisições

## 📋 Pré-requisitos

- Java JDK 17 ou superior
- Instância do Oracle Database
- Maven ou ferramenta de build similar
- Git

## 📡 Endpoints da API

### Autenticação
- `POST /auth/login` - Autenticação de usuário
- `POST /auth/pacientes/register` - Registro de paciente

### Pacientes
- `GET /pacientes` - Lista todos os pacientes
- `GET /pacientes/{id}` - Obtém paciente por ID
- `POST /pacientes` - Cria novo paciente
- `PUT /pacientes/{id}` - Atualiza informações do paciente

### Consultas
- `GET /consultas` - Lista todas as consultas
- `GET /consultas/paciente/{id}` - Lista todas as consultas de um paciente
- `POST /consultas` - Agenda nova consulta
- `PUT /consultas/status` - Atualiza status da consulta

### Especialidades Médicas
- `GET /especialidades` - Lista todas as especialidades
- `GET /especialidades/disponibilidade` - Verifica disponibilidade por especialidade

### Profissionais de Saúde
- `GET /profissionais` - Lista todos os profissionais de saúde

## 💾 Banco de Dados

O sistema utiliza Oracle Database para persistência de dados. O esquema do banco inclui tabelas para:
- Pacientes
- Consultas
- Especialidades Médicas
- Profissionais de Saúde
- Disponibilidade

## 📁 Estrutura do Projeto

```
pathmed/
├── src/
│   ├── Main.java
│   └── br/com/pathmed/
│       ├── controller/    # Manipuladores de requisições HTTP
│       ├── dao/          # Objetos de Acesso a Dados
│       ├── model/        # Modelos de dados
│       ├── service/      # Lógica de negócios
│       └── util/         # Classes utilitárias
├── lib/                  # Dependências externas
├── bin/                  # Classes compiladas
└── PathmedAPI/           # Documentação da API
```

## 👥 Autores

- Lucas Grillo AlcÂntara - RM 561413
- Augusto Buguas Rodrigues - RM 563858
- Pietro Abrahamian - RM 561469

---

Feito com ❤️ pela Equipe PathMed
