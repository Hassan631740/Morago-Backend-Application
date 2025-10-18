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
            
            This API supports advanced pagination and filtering capabilities for all GET endpoints.
            
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
            
            #### Basic Pagination
            ```
            GET /api/users?page=0&size=10&sortBy=username&sortDirection=asc
            ```
            
            #### Search with Pagination
            ```
            GET /api/users?page=0&size=10&search=john
            ```
            
            #### Filter by Role
            ```
            GET /api/users?page=0&size=10&filters[role]=ADMINISTRATOR
            ```
            
            #### Date Range Filtering
            ```
            GET /api/users?page=0&size=10&dateFrom=2023-01-01T00:00:00&dateTo=2023-12-31T23:59:59
            ```
            
            #### Complex Filtering
            ```
            GET /api/users?page=0&size=10&search=john&filters[role]=USER&filters[active]=true
            ```
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
