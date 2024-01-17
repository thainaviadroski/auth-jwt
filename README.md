# Auth with JWT

### Stacks do projeto:

![Static Badge](https://img.shields.io/badge/spring-boot?style=for-the-badge&color=green)
![Static Badge](https://img.shields.io/badge/java-lang?style=for-the-badge&color=red)

## Implementando sistema de login baseado no JWT

A implementção do JWT utilizando o Spring framework, ao visualizarmos apenas o codigo é muito simples, porém requer um
bom entedimento funcionamento das requisições web e do framework. Spring-boot é um framework composto por diversas
ferramentas que compoem seu eco sistema, para gestão de segurança das aplicações temos o Spring Security.

Na implementação presente nesse repositório, vemos de forma bastante simples

Dependencias usadas além do Spring Web:

```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
```

Quando iniciamos um projeto spring utilizando a dependencia Spring Security ela irá iniciar utilizando suas configurações default, habilitando uma rota de login, e irá bloquear todas as requisções as suas rotas.

```java

@RestController
public class HomeController {
    @GetMapping("home")
    public String getHome() {
        return "Home";
    }

    @PostMapping("login")
    public ResponseEntity<AuthToken> login(@RequestBody Users user) {
        System.out.println(user.toString());
        if (user.getLogin().equals("jake") && user.getPassword().equals("banana")) {
            return ResponseEntity.ok(TokenUtil.encodeToken(user));
        }
        return ResponseEntity.status(403).build();
    }

    @GetMapping("products")
    public String getProduct() {
        return "Products";
    }
}

```
No bloco de código acima, temos três rotas, a primeira rota **/home** será de livre acesso, qualquer um pode realizar uma requição para elas, a rota **/products** só podera ser acessada se o usuário possuir um token valido, e a rota **/login** que iremos utilizar para gerar nosso token JWT.



```java 
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(auth -> auth.requestMatchers("home").permitAll())
                .authorizeRequests(auth -> auth.requestMatchers("login").permitAll())
                .authorizeRequests(auth -> auth.anyRequest().authenticated())
                .cors(withDefaults());
        http.addFilterBefore(new SecurityFilters(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```
No bloco acima realizamos as configurações necessarias para utilizar o Spring Security, de acordo com as necessidades do nosso projeto.
No método ```addFilterBefore``` adicionamos quais serão os filtros que iremos utilizar, e quais são as regras para validar nossas requisições que precisam de algum tipo de autenticação ou autorização para serem acessadas.



```java
public class SecurityFilters extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filter) throws ServletException, IOException {
        if (req.getHeader("Authorization") != null) {
            Authentication auth = TokenUtil.decodeToken(req);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }else{
                ErrorDTO error = new ErrorDTO(401, "Usuario sem a permicao adquada!");
                res.setStatus(error.status());
                res.setContentType("application/json");
                ObjectMapper mapper = new ObjectMapper();
                res.getWriter().println(mapper.writeValueAsString(error));
                res.getWriter().flush();
                return;
            }
        }
        filter.doFilter(req, res);
    }
}
```
Na classe SecurityFilters realizamos a implementação de nosso filtro personalizado. Para realizamos a validação e geração do nosso token JWT, criamos a classe TokenUtil que tem como principal responsabilidade gerar e validar os tokens JWT da aplicação.  