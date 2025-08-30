# TaskBoard - Gerenciamento de Tarefas
Descrição

Projeto Java para criar e gerenciar boards de tarefas, persistindo boards, colunas e cards no MySQL.

# Para executar
Pré-requisitos:
Java JDK 21+

MySQL / MariaDB rodando com:

Banco: task_board

Usuário: board_user

Senha: senha123

MySQL Connector/J (biblioteca Java para conectar com MySQL)
```
git clone https://github.com/JonasAlv/TaskBoard.git
cd TaskBoard

javac -cp libs/mysql-connector-java-8.1.1.jar src/*.java -d out
java -cp out:libs/mysql-connector-java-8.1.1.jar Main


```
seleciona opção 1 para criar board
para visualizar seu board:

mariadb -u board_user -p 
(senha = senha123)

USE task_board;

SELECT * FROM boards;

o mesmo para colunas e cards...
