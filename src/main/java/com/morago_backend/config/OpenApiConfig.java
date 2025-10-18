package com.morago_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Morago Backend API")
                .description("""
                    ## Morago Backend REST API Documentation
                    
                    This API provides comprehensive endpoints for the Morago translation platform, including:
                    
                    ### Key Features:
                    - **User Management**: User registration, authentication, and profile management
                    - **Translator Services**: Translator profiles, verification, and availability management
                    - **Admin Features**: Account blocking/activation, profile verification
                    - **File Storage**: Avatar and document upload/download (AWS S3 + Local)
                    - **Real-time Communication**: WebSocket support for live translation sessions
                    - **Financial Operations**: Deposits, withdrawals, and balance management with automatic debt payment
                    - **Transaction Tracking**: Complete audit trail for all financial operations (deposits, withdrawals, call payments, commissions)
                    - **Debt Management**: Automated debt tracking, payment, and user status management
                    - **Rating System**: User and translator ratings
                    - **Multilingual Support**: Language and theme management
                    - **Call Records**: Track translation call sessions with automatic balance settlement
                    
                    ### Financial System:
                    - **Deposits**: Automatic balance crediting with debt payment priority
                    - **Withdrawals**: Balance verification with debtor restrictions
                    - **Transactions**: Unified tracking for all financial operations (DEPOSIT, WITHDRAWAL, CALL_PAYMENT, CALL_EARNING, COMMISSION, REFUND, ADJUSTMENT)
                    - **Debt System**: Automatic debt creation, tracking, and payment from deposits
                    
                    ### Transaction Types:
                    - `DEPOSIT`: Money added to account
                    - `WITHDRAWAL`: Money withdrawn from account
                    - `CALL_PAYMENT`: Payment for translation call (debit for caller)
                    - `CALL_EARNING`: Earning from translation (credit for interpreter)
                    - `COMMISSION`: Platform commission (debit)
                    - `REFUND`: Refund or debt payment
                    - `ADJUSTMENT`: Manual balance adjustment by admin
                    
                    ### Authentication:
                    All endpoints (except public auth endpoints) require JWT authentication.
                    Use the **Authorize** button above to add your JWT token.
                    
                    ### Roles & Permissions:
                    - **ADMINISTRATOR**: Full system access including deposits, withdrawals, debtor management
                    - **INTERPRETER**: Translator-specific features, can request withdrawals (if no unpaid debts)
                    - **CLIENT**: Customer-specific features, can make deposits
                    
                    ### Balance & Debt Rules:
                    1. Deposits automatically pay off debts before crediting balance
                    2. Users with unpaid debts cannot request withdrawals
                    3. All balance changes create transaction records
                    4. Balance before/after tracked for audit purposes
                    
                    ### Query Capabilities:
                    - Filter deposits/withdrawals by user, status, and date range
                    - View complete transaction history with filtering
                    - Generate financial reports and statistics
                    - Track pending operations and debt status
                    
                    ### Response Format:
                    All responses follow standard REST conventions with appropriate HTTP status codes.
                    """)
                .version("v2.0.0")
                .contact(new Contact()
                    .name("Morago Support")
                    .email("support@morago.com")
                    .url("https://morago.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://morago.com/license")))
            .externalDocs(new ExternalDocumentation()
                .description("Full Documentation & Guides")
                .url("https://docs.morago.com"))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local Development Server (" + activeProfile + " profile)"),
                new Server()
                    .url("https://api.morago.com")
                    .description("Production Server"),
                new Server()
                    .url("https://staging-api.morago.com")
                    .description("Staging Server")
            ))
            .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
            .components(new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token obtained from /api/auth/login endpoint. Format: 'Bearer <token>'"))
            );
    }
}


