package com.morago_backend.security;

/**
 * Role-Based Access Control (RBAC) Documentation
 * 
 * This document describes the role-based access control system implemented in the Morago Backend application.
 * 
 * ROLES:
 * ======
 * 
 * 1. CLIENT
 *    - Description: Users who request interpretation services
 *    - Permissions:
 *      - View translator profiles
 *      - Create and view call records
 *      - Rate interpreters
 *      - Manage their own profile
 *      - View their own notifications
 * 
 * 2. INTERPRETER
 *    - Description: Users who provide interpretation services
 *    - Permissions:
 *      - Manage translator profile
 *      - View and respond to call records
 *      - Rate clients
 *      - Manage their own profile
 *      - View their own notifications
 * 
 * 3. ADMINISTRATOR
 *    - Description: System administrators with full access
 *    - Permissions:
 *      - Full access to all resources
 *      - Manage users and roles
 *      - Manage system resources (categories, languages, themes)
 *      - Manage financial operations (deposits, withdrawals)
 *      - View all call records and ratings
 * 
 * IMPLEMENTATION:
 * ==============
 * 
 * 1. Entity Level:
 *    - Role entity with RoleType enum
 *    - User entity with many-to-many relationship to Role
 *    - RoleConstants class for role names and descriptions
 * 
 * 2. Security Configuration:
 *    - SecurityConfig with role-based URL patterns
 *    - Method-level security with annotations
 *    - Custom permission evaluator
 * 
 * 3. Annotations:
 *    - @RequireRole: Requires specific role(s)
 *    - @RequireAnyRole: Requires any of the specified roles
 *    - @RequireAllRoles: Requires all specified roles
 * 
 * 4. Services:
 *    - RoleBasedAccessControlService: Runtime role checking
 *    - UserRoleManagementService: Role assignment and management
 *    - DataInitializationService: Default role creation
 * 
 * 5. Controllers:
 *    - RoleController: Role management (Admin only)
 *    - UserRoleManagementController: User role assignment (Admin only)
 *    - All other controllers have role-based access control
 * 
 * USAGE EXAMPLES:
 * ==============
 * 
 * 1. Method-level security:
 *    @RequireRole("ADMINISTRATOR")
 *    public void adminOnlyMethod() { ... }
 * 
 *    @RequireAnyRole({"CLIENT", "INTERPRETER"})
 *    public void clientOrInterpreterMethod() { ... }
 * 
 * 2. Runtime role checking:
 *    @Autowired
 *    private RoleBasedAccessControlService rbacService;
 * 
 *    if (rbacService.isClient()) {
 *        // Client-specific logic
 *    }
 * 
 * 3. URL-based security (in SecurityConfig):
 *    .requestMatchers("/api/roles/**").hasRole("ADMINISTRATOR")
 *    .requestMatchers("/api/translator-profiles/**").hasAnyRole("INTERPRETER", "ADMINISTRATOR")
 * 
 * SECURITY CONSIDERATIONS:
 * =======================
 * 
 * 1. Role validation is performed at multiple levels:
 *    - URL pattern matching
 *    - Method-level annotations
 *    - Runtime service checks
 * 
 * 2. Default roles are automatically created on application startup
 * 
 * 3. Role assignments are managed through dedicated services
 * 
 * 4. All role operations are logged and auditable
 * 
 * 5. JWT tokens include role information for stateless authentication
 * 
 * API ENDPOINTS:
 * =============
 * 
 * Role Management (Admin only):
 * - GET /api/roles - Get all roles
 * - POST /api/roles - Create role
 * - PUT /api/roles/{id} - Update role
 * - DELETE /api/roles/{id} - Delete role
 * 
 * User Role Management (Admin only):
 * - POST /api/user-roles/{userId}/assign/{roleName} - Assign role
 * - DELETE /api/user-roles/{userId}/remove/{roleName} - Remove role
 * - PUT /api/user-roles/{userId}/set-roles - Set user roles
 * - GET /api/user-roles/{userId}/roles - Get user roles
 * - GET /api/user-roles/by-role/{roleName} - Get users by role
 * - GET /api/user-roles/clients - Get all clients
 * - GET /api/user-roles/interpreters - Get all interpreters
 * - GET /api/user-roles/administrators - Get all administrators
 */
public class RoleBasedAccessControlDocumentation {
    // This class serves as documentation only
}
