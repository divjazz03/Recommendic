

<h2 align="center">A Medical Consultation and Recommendation Application </h2>

## 📑 Table of Contents

1. 🤖 [Introduction](#-introduction)
2. 🔋 [Features](#-features)
3. 🔩 [Tech Stack](#-tech-stack) 
4. 💃 [Quick Start](#-quick-start)
5. 🕸 [Snippets](#-snippets)


## 🤖 Introduction
The idea of this application is inspired by the idea and observation of the hassles encountered in medical consultations due to distance and or location.
This application brings the patients to certified medical practitioners of various disciplines from anywhere from around the world or even from their own communities for physical consultation and check-up scheduling.
This solves the problem of finding certified medical practitioners especially in an area where the admin is unfamiliar with.
This app emphasises on ease and great admin experiences for all our users.</p>

## 🔋 Features
- 👉 **User**: This encapsulates all the users of the platform of which include the `patients`, `admins`,`super_admins` and certified `consultants`.
- 👉 **Registration**: This is the conversion of a guest to an authenticated user. The guest can be converted to a `PATIENT` or a `CONSULTANT` at a time. To qualify as a verified `CONSULTANT`, the `ADMIN` assigned must verify the authenticity of the certification, or in later versions can be verified by artificial intelligence.
- 👉 **Login**: This logs in the users into the platform. It uses JWT Authentication to verify user identity. It makes the feeds and data displayed to them personalized according to their recent activities or authorization level.
- 👉 **Recommendation**: This is unique to the `patients`. It recommends consultants or articles according to search patterns, location or recent activities.
- 👉 **Real Time Consultation and Chat**: This involves real time consultation/chats with consultants whom the admin has started a consultation with. It utilizes web sockets. The admin can have a real-time conversation with the practitioner of their choosing about topics they are interested in.
- 👉 **Admin Registration**: An `ADMIN` can be registered as an admin by a `SUPER_ADMIN` and an automatically generated password is assigned to that admin.
- 👉 **Consultant Certification**: Admins are assigned the certificates of the consultants that haven't been certified. Once the assigned admin certifies the certificates. The Consultant is automatically verified.
- 👉 **File upload**: The Consultant can upload pdf documents or clear images of their certificates. Everyone consultant and patients need to upload their profile pictures for visual identification.
- 👉 **Email Notifications**: Implements email notifications for account confirmation and reset of passwords

## 🔩 Tech Stack
- SpringBoot
- Hibernate
- Java
- Docker
- Postgresql

## 💃 Quick Start
Follow these steps to set up the project locally on your machine:

**Prerequisites**
Make sure you have the following installed on your machine:

- [Docker](https://www.docker.com)
- [Docker Compose](#installing-docker-compose)
- [PostMan](https://www.postman.com)


#### Installing docker and docker-compose
- Set up Docker's `apt` repository.
```bash
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
```
if you use an Ubuntu derivative distro, such as Linux Mint, you may need to use `UBUNTU_CODENAME` instead of `VERSION_CODENAME`.

- Install the Docker packages
```bash
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```
- Verify that the Docker engine installation was successful
```bash
sudo docker run hello-world
```

- Update the package index, and install the latest version of Docker Compose
```bash
sudo apt-get update
sudo apt-get install docker-compose-plugin
```
- Finally, verify that the Docker compose is installed correctly by checking the version
```bash
docker compose version
```

🔰 **[For more info on installation in other distros](https://docs.docker.com/compose/install/linux/#install-using-the-repository)**

**Cloning the Repository**
```bash
git clone https://github.com/divjazz03/recommendic
```
**Set Up Environment Variables**

Create a new file named `.env` in the root of your project and add the following content
```env
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_DB=
SECRET={Make it as long as possible}
EMAIL_HOST=
EMAIL_PORT=
EMAIL_ID=
EMAIL_PASSWORD=
VERIFY_EMAIL_HOST=
```

**Build the app image**
```bash
docker build --tag=recommendic:latest
```

**Get it up and running**
```bash
docker compose up
``` 
**or**
```bash
docker-compose up --remove-ophans
```

**Now [localhost port 8080](http://localhost:8080) is ready to receive requests**

## 🕸 Snippets
<details>
<summary><code>application.yaml</code></summary>

```yml
spring:
  profiles:
    active: ${ACTIVE_PROFILE:dev}
  jackson:
    default-property-inclusion: non_null
    serialization:
      fail-on-empty-beans: false
      close-closeable: true
      flush-after-write-value: true
      write-date-keys-as-timestamps: false
    deserialization:
      fail-on-unknown-properties: false

  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB

  sql:
    init:
      mode: always
      continue-on-error: false
      schema-locations: classpath:/sql/schema.sql
      data-locations: classpath:/sql/data.sql


  datasource:
    password: ${POSTGRES_PASSWORD}
    username: ${POSTGRES_USER}
    url: ${POSTGRES_URL}
    driver-class-name: org.postgresql.Driver
    hikari:
      auto-commit: false
  jpa:
    hibernate:
      ddl-auto: validate
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    open-in-view: false
    properties:
      hibernate:
        globally_quoted_identifiers: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  mail:
    host: ${EMAIL_HOST}
    port: ${EMAIL_PORT}
    username: ${EMAIL_ID}
    password: ${EMAIL_PASSWORD}
    default-encoding: UTF-8
    properties:
      mail:
        mime:
          charset: UTF
        smtp:
            writetimeout: 10000
            connectiontimeout: 10000
            timeout: 10000
            auth: true
            starttls:
            enable: true
            required: true
    verify:
      host: ${VERIFY_EMAIL_HOST}
jwt:
  expiration: ${JWT_EXPIRATION}
  secret: ${JWT_SECRET}

file:
  upload:
  implementation: ${FILE_UPLOAD_IMPL}
```
</details>

<details><summary><code>JWT Authentication Service Implementation</code></summary>

```java 

@Service
public class JwtServiceImpl extends JwtConfiguration implements JwtService {
    private final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);
    private final GeneralUserService userService;


    private final Supplier<SecretKey> keySupplier = () -> Keys
            .hmacShaKeyFor(
                    Decoders.BASE64.decode(getSecret())
            );

    private final Function<String, Claims> claimsFunction = token ->
            Jwts.parser()
                    .verifyWith(keySupplier.get())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

    private final Function<String, String> subject = token -> getClaimsValue(token, Claims::getSubject);

    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName) ->
            Optional.of(Arrays.stream(
                            request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} :
                                    request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                    .map(Cookie::getValue)
                    .findAny()).orElse(empty());
    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookie = (request, cookieName) ->
            Optional.of(Arrays.stream(
                            request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} :
                                    request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                    .findAny()).orElse(empty());

    private final Supplier<JwtBuilder> builder = () ->
            Jwts.builder()
                    .header().add(Map.of(TYPE, JWT_TYPE))
                    .and()
                    .audience().add("RECOMMENDIC")
                    .and()
                    .id(String.valueOf(UUID.randomUUID()))
                    .issuedAt(Date.from(Instant.now()))
                    .notBefore(new Date())
                    .signWith(keySupplier.get(), Jwts.SIG.HS512);

    private final BiFunction<User, TokenType, String> buildToken = (user, type) ->
            Objects.equals(type, ACCESS) ? builder.get()
                    .subject(user.getEmail())
                    .claim(PERMISSIONS, user.getRole().getPermissions())
                    .claim(ROLE, user.getRole().getName())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact() : builder.get()
                    .subject(user.getEmail())
                    .compact();

    private final TriConsumer<HttpServletResponse, User, TokenType> addCookie = (response, user, type) -> {
        switch (type) {
            case ACCESS -> {
                var accessToken = createToken(user, Token::getAccess);
                var cookie = new Cookie(type.getValue(), accessToken);
                cookie.setHttpOnly(true);
                // cookie.setSecure(true);
                cookie.setMaxAge(2 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
            case REFRESH -> {
                var refreshToken = createToken(user, Token::getRefresh);
                var cookie = new Cookie(type.getValue(), refreshToken);
                cookie.setHttpOnly(true);
                // cookie.setSecure(true);
                cookie.setMaxAge(60 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
        }
    };

    private final BiConsumer<HttpServletResponse, User> addHeader = (response, user) -> {
        var accessToken = createToken(user,Token::getAccess);
        response.addHeader("Authorization", String.format("Bearer %s", accessToken));
    };

    public <T> T getClaimsValue(String token, Function<Claims, T> claims) {
        return claimsFunction.andThen(claims).apply(token);
    }


    public Function<String, List<? extends GrantedAuthority>> stringToAuthorities = token ->
            commaSeparatedStringToAuthorityList(new StringJoiner(PERMISSION_DELIMITER)
                    .add(claimsFunction.apply(token).get(PERMISSIONS, String.class)).toString());




    public JwtServiceImpl(GeneralUserService userService) {
        this.userService = userService;
    }

    @Override
    public String createToken(User user, Function<Token, String> tokenFunction) {
        var token = Token.builder()
                .access(buildToken.apply(user, ACCESS))
                .refresh(buildToken.apply(user, REFRESH))
                .build();
        return tokenFunction.apply(token);
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest httpServletRequest, String cookieName) {
        return extractToken.apply(httpServletRequest, cookieName);
    }

    @Override
    public void addCookie(HttpServletResponse response, User user, TokenType type) {
        addCookie.accept(response, user, type);
    }

    @Override
    public void addHeader(HttpServletResponse response, User user, TokenType type) {
        addHeader.accept(response,user);
    }

    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(
                TokenData.builder()
                        .valid(
                                Objects.equals(userService.retrieveUserByUsername(subject.apply(token)).getEmail(), claimsFunction.apply(token).getSubject())
                        )
                        .expired(Instant.now().isAfter(getClaimsValue(token, Claims::getExpiration).toInstant()))
                        .authorities(List.of(new SimpleGrantedAuthority((String) claimsFunction.apply(token).get("permissions"))))
                        .claims(claimsFunction.apply(token))
                        .user(userService.retrieveUserByUsername(subject.apply(token)))
                        .build()
        );
    }

    @Override
    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        var optionalCookie = extractCookie.apply(request, cookieName);
        if (optionalCookie.isPresent()) {
            var cookie = optionalCookie.get();
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    @Override
    public boolean validateToken(HttpServletRequest request) {
        var token = extractToken(request , ACCESS.getValue());
        if (token.isPresent()) {
            return getTokenData(token.get(), TokenData::isValid) ;
        }
        return false;
    }


}

```
</details>
