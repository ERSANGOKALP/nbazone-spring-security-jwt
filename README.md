# nbazone-spring-security-jwt
📊 A Spring Boot-based project that provides comprehensive NBA player statistics with JWT authentication and role-based access control.

# 🏀 NBAZone

NBAZone, NBA oyuncularına ait istatistikleri yönetebileceğiniz modern, kullanıcı dostu ve güvenli bir web uygulamasıdır. Bu proje, Spring Boot ile geliştirilmiş monolitik bir yapıya sahiptir ve JWT tabanlı cookie-based authentication ile korunmaktadır.

## 📌 Özellikler

- 🧾 Oyuncu CRUD işlemleri (Create, Read, Update, Delete)
- 🔒 Cookie-Based JWT Authentication (Login, Register, Role-based authorization)
- 🧪 Unit Test & Integration Test (Mockito, MockMvc, @WebMvcTest, @DataJpaTest)
- 📦 DTO, Service, Repository, EntityMapper gibi kurumsal mimariye uygun katmanlı yapı
- 🌐 Swagger/OpenAPI ile API dokümantasyonu
- 🛡️ Spring Security ile yetkilendirme ve kimlik doğrulama
- 📊 Oyuncu istatistikleri: sayı, ribaund, asist, yaş, takım bilgisi vs.

## 🧱 Kullanılan Teknolojiler

| Katman         | Teknoloji                           |
|----------------|-------------------------------------|
| Backend        | Java 21, Spring Boot                |
| Güvenlik       | Spring Security, JWT (Cookie-Based) |
| Test           | JUnit 5, Mockito, MockMvc           |
| Veritabanı     | MySQL                               |
| API Dokümantasyon | SpringDoc / Swagger UI           |
| IDE            | IntelliJ IDEA                       |
| Diğer          | Lombok, MapStruct, Validation       |

## 🔐 Authentication & Authorization

- JWT tabanlı cookie-based authentication
- `/api/auth/signin` , `/api/v1/signup` , `/api/auth/signout` , `/api/auth/username` ve `/api/auth/user`  uç noktaları
- Role-Based Authorization:
  - `ADMIN` ve `USER` rollerine göre endpoint erişimi
- Güvenlik filtreleri `JwtAuthenticationFilter` aracılığıyla kontrol edilir.

## 📁 Proje Yapısı

nbazone/
├── config                                 
├── dto                                               
│ └── PlayerRequestDto                                                                          
│ └── PlayerResponseDto                                                   
├── exception                                                          
│ └── ErrorResponse                                                                  
│ └── GlobalExeptionHandler                                                                      
│ └── PlayerNotFoundExeption                                                                       
│ └── TeamNotFoundExeption                                                                                                                                      
├── mapper                                                               
│ └── EntityMapper                                                         
├── player                                                                            
│ └── Player                                                                                
│ └── PlayerController                                                                 
│ └── PlayerRepository                                                                                 
│ └── PlayerService                                                                           
│ └── PlayerServiceImpl                                                                          
├── security                                                                                            
│ └── config                                                                                                         
│   └── RoleInitializer                                                                                                
│   └── SecurityConfig                                                                                       
│ └── jwt                                                                                                           
│   └── AuthEntryPointJwt                                                                                           
│   └── JwtAuthenticationFilter                                                                              
│   └── JwtUtils                                                                                           
│ └── request                                                                                                                  
│   └── LoginRequest                                                                             
│   └── SignupRequest                                                             
│ └── response                                                                                     
│   └── MessageResponse                                                                          
│   └── UserJwtInfoResponse                                                                                  
│ └── userDetail                                                                                   
│   └── CustomUserDetails                                                                   
│   └── CustomUserDetailsService                                                                   
├── user                                                                            
│ └── AppRole                                                                                             
│ └── AuthController                                                                       
│ └── Role                                                                         
│ └── RoleRepository                                                                         
│ └── User                                                                                          
│ └── UserRepository                                                                                              


## 🧪 Testler

- Service katmanında unit testler: `@ExtendWith(MockitoExtension.class)` ,`@WebMvcTest`, `MockMvc` kullanılarak test edildi.
- Tüm testler kurumsal yapıya uygun olarak yapılandırılmıştır.

## 🚀 Uygulamayı Çalıştırma

### Gerekli Araçlar:
- Java 21
- IntelliJ IDEA (önerilir)
- Maven

### Adımlar:

```bash
git clone  https://github.com/ERSANGOKALP/nbazone-spring-security-jwt.git
cd nbazone-spring-security-jwt
./mvnw spring-boot:run

### Swagger UI:
Uygulama çalıştıktan sonra Swagger'a şu adresten ulaşabilirsiniz:
http://localhost:8080/swagger-ui.html

✍️ Geliştirici
ersandev

GitHub: https://github.com/ERSANGOKALP

LinkedIn: https://www.linkedin.com/in/ersan-g%C3%B6kalp-2883101ba/


