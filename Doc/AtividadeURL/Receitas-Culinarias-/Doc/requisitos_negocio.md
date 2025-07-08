Objetivo Geral:
Desenvolver uma plataforma online (API base para futura aplicação mobile/web) voltada para compartilhamento, busca, avaliação e organização de receitas culinárias, promovendo interação entre os usuários.

Requisitos Funcionais (O que o sistema deve fazer):

Módulo de Usuário:
    1. Permitir o cadastro de usuários, com nome de usuário, senha e tipo de usuário (padrão: "USUARIO").

    2. Permitir login de usuários com validação de nome de usuário e senha.

    3. Permitir que o usuário atualize seu perfil (nome de usuário e senha).

    4. Diferenciar usuários entre usuário comum e administrador, através do campo tipoUsuario.


Módulo de Receitas:
    5. Permitir que usuários criem novas receitas, incluindo: título, descrição, tempo de preparo, categoria (como texto simples) e ingredientes.

    6. Permitir que usuários adicionem ingredientes à receita.

    7. Permitir que usuários removam ingredientes de uma receita.

    8. Permitir que usuários adicionem comentários em uma receita.

    9. Permitir que usuários adicionem avaliações (nota de 1 a 5) em uma receita.

    10. Permitir que o criador da receita edite os dados da sua própria receita.

    11. Permitir que o criador da receita exclua a própria receita.


Módulo de Ingredientes:
    12. Permitir edição dos dados de um ingrediente (nome e quantidade).

    13. Permitir remoção de um ingrediente específico de uma receita.


Módulo de Comentários:
    14. Permitir que usuários editem seus próprios comentários.

    15. Permitir que usuários excluam seus próprios comentários.

    16. Permitir que administradores excluam qualquer comentário.


Módulo de Avaliações:
    17. Permitir que usuários avaliem receitas com uma nota de 1 a 5 estrelas.

    18. Permitir que usuários alterem suas próprias avaliações.

    19. Permitir que usuários excluam suas próprias avaliações.

    20. Garantir que cada usuário possa avaliar a mesma receita apenas uma vez (mas possa editar depois).


Módulo de Favoritos:
    21. Permitir que usuários adicionem receitas aos favoritos.

    22. Permitir que usuários removam receitas dos favoritos.

    23. Permitir que usuários visualizem sua lista de favoritos.


Módulo Administrativo (Permissões exclusivas do tipo "ADMIN"):
    24. Permitir que administradores excluam qualquer receita do sistema.

    25. Permitir que administradores excluam qualquer comentário.

    26. Permitir que administradores visualizem a lista completa de usuários.

    27. Permitir que administradores excluam contas de usuários, se necessário.


Requisitos Não Funcionais:
    • A API deve ser RESTful, com endpoints claros.

    • Implementar autenticação e controle de acesso baseado em token JWT.

    • Senhas devem ser armazenadas com hash seguro.

    • As listagens (ex: receitas, favoritos, comentários) devem ter paginação.

    • Deve haver validação de dados (ex: impedir avaliação acima de 5, impedir campos obrigatórios vazios).

    • O código deve ser organizado em camadas (Controller, Service, Repository, Model).

    • Uso de logs de erros e auditoria de ações administrativas.


Melhorias Futuras (não obrigatórias nesta fase):
    • Upload de fotos das receitas.

    • Sistema de busca avançada (filtros por múltiplos ingredientes, tempo, etc).

    • Histórico de ações do usuário.

    • Notificações de novas avaliações ou comentários.

    • Integração com redes sociais.

