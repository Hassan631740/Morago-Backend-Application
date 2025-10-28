package com.morago_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for pagination and filtering documentation
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Morago Backend API",
        version = "1.0.0",
        description = """
            ## Pagination and Filtering API Documentation
            
            This API supports advanced pagination and filtering capabilities for SELECT GET endpoints only.
            Not all endpoints support pagination. Check individual endpoint documentation for details.
            
            ### Endpoints with Pagination Support
            - `/api/admin/users` - Get all users (admin only)
            - `/api/translator-profiles` - Get all translator profiles
            - `/api/languages` - Get all languages
            - `/api/categories` - Get all categories
            
            ### Endpoints WITHOUT Pagination
            - `/api/files` - Returns all files
            - `/api/themes` - Returns all themes
            - `/api/ratings` - Returns all ratings
            - `/api/calls` - Returns all call records
            - Most other GET endpoints return simple lists
            
            ### Pagination Parameters
            - **page**: Page number (0-based, default: 0)
            - **size**: Number of items per page (1-100, default: 10)
            - **sortBy**: Field to sort by (default: "id")
            - **sortDirection**: Sort direction - "asc" or "desc" (default: "asc")
            
            ### Filtering Parameters
            - **search**: Text search across relevant fields
            - **filters**: Map of field-specific filters
            - **dateFrom**: Start date for date range filtering (ISO format)
            - **dateTo**: End date for date range filtering (ISO format)
            
            ### Response Format
            All paginated responses follow this structure:
            ```json
            {
                "content": [...],           // Array of items
                "page": 0,                  // Current page number
                "size": 10,                 // Items per page
                "totalElements": 100,       // Total items across all pages
                "totalPages": 10,           // Total number of pages
                "first": true,              // Is this the first page?
                "last": false,              // Is this the last page?
                "numberOfElements": 10,     // Items in current page
                "hasNext": true,            // Are there more pages?
                "hasPrevious": false        // Are there previous pages?
            }
            ```
            
            ### Example Usage
            
            #### Basic Pagination (supported endpoints only)
            ```
            GET /api/admin/users?page=0&size=10&sortBy=username&sortDirection=asc
            GET /api/languages?page=0&size=10&sortBy=name&sortDirection=asc
            GET /api/categories?page=0&size=10&sortBy=name&sortDirection=asc
            ```
            
            #### Search with Pagination
            ```
            GET /api/admin/users?page=0&size=10&search=john
            ```
            
            #### Filter Parameters (where supported)
            ```
            GET /api/admin/users?page=0&size=10&filters[role]=ADMINISTRATOR
            GET /api/languages?page=0&size=10&search=korean
            ```
            
            ### Important Notes
            - Only endpoints documented as supporting pagination accept pagination parameters
            - Endpoints returning simple lists ignore pagination parameters
            - Always check the OpenAPI/Swagger documentation for each endpoint
            """,
        contact = @Contact(
            name = "Morago Development Team",
            email = "dev@morago.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Development server"),
        @Server(url = "https://api.morago.com", description = "Production server")
    },
    tags = {
        @Tag(name = "User Management", description = "APIs for managing users with pagination and filtering"),
        @Tag(name = "Language Management", description = "APIs for managing languages with pagination and filtering"),
        @Tag(name = "Category Management", description = "APIs for managing categories with pagination and filtering"),
        @Tag(name = "Example API", description = "Example APIs demonstrating pagination and filtering capabilities")
    }
)
public class PaginationOpenApiConfig {
    // Configuration class for OpenAPI documentation
}
