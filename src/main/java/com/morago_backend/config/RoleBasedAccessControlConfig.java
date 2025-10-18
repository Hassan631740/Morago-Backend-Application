package com.morago_backend.config;

import com.morago_backend.entity.RoleConstants;
import com.morago_backend.service.RoleBasedAccessControlService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class RoleBasedAccessControlConfig {

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = 
            new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return expressionHandler;
    }

    @Bean
    public RoleBasedAccessControlService roleBasedAccessControlService() {
        return new RoleBasedAccessControlService();
    }

    /**
     * Custom permission evaluator for more complex permission checks
     */
    public static class CustomPermissionEvaluator implements org.springframework.security.access.PermissionEvaluator {
        
        @Override
        public boolean hasPermission(org.springframework.security.core.Authentication authentication, 
                                   Object targetDomainObject, Object permission) {
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String permissionStr = permission.toString();
            
            // Define permission mappings based on roles
            switch (role) {
                case RoleConstants.ADMINISTRATOR:
                    return true; // Administrators have all permissions
                case RoleConstants.INTERPRETER:
                    return isInterpreterPermission(permissionStr);
                case RoleConstants.CLIENT:
                    return isClientPermission(permissionStr);
                default:
                    return false;
            }
        }
        
        @Override
        public boolean hasPermission(org.springframework.security.core.Authentication authentication, 
                                   java.io.Serializable targetId, String targetType, Object permission) {
            return hasPermission(authentication, null, permission);
        }
        
        private boolean isInterpreterPermission(String permission) {
            return permission.equals("READ") || 
                   permission.equals("WRITE") ||
                   permission.contains("TRANSLATOR_PROFILE") ||
                   permission.contains("RATING") ||
                   permission.contains("CALL_RECORD");
        }
        
        private boolean isClientPermission(String permission) {
            return permission.equals("READ") || 
                   permission.contains("TRANSLATOR_PROFILE") ||
                   permission.contains("RATING") ||
                   permission.contains("CALL_RECORD");
        }
    }
}
