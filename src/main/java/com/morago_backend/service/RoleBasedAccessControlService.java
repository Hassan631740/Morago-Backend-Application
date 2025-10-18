package com.morago_backend.service;

import com.morago_backend.entity.RoleConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleBasedAccessControlService {

    /**
     * Check if the current user has a specific role
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleName));
    }

    /**
     * Check if the current user has any of the specified roles
     */
    public boolean hasAnyRole(String... roleNames) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Set<String> userRoles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());
        
        for (String roleName : roleNames) {
            if (userRoles.contains(roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the current user has all of the specified roles
     */
    public boolean hasAllRoles(String... roleNames) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Set<String> userRoles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());
        
        for (String roleName : roleNames) {
            if (!userRoles.contains(roleName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the current user is a client
     */
    public boolean isClient() {

        return hasRole(RoleConstants.CLIENT);
    }

    /**
     * Check if the current user is an interpreter
     */
    public boolean isInterpreter() {

        return hasRole(RoleConstants.INTERPRETER);
    }

    /**
     * Check if the current user is an administrator
     */
    public boolean isAdministrator() {

        return hasRole(RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user is either an interpreter or administrator
     */
    public boolean isInterpreterOrAdmin() {

        return hasAnyRole(RoleConstants.INTERPRETER, RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can access client resources
     */
    public boolean canAccessClientResources() {

        return hasAnyRole(RoleConstants.CLIENT, RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can access interpreter resources
     */
    public boolean canAccessInterpreterResources() {
        return hasAnyRole(RoleConstants.INTERPRETER, RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can access admin resources
     */
    public boolean canAccessAdminResources() {

        return hasRole(RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can manage users
     */
    public boolean canManageUsers() {

        return hasRole(RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can manage roles
     */
    public boolean canManageRoles() {

        return hasRole(RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can view all call records
     */
    public boolean canViewAllCallRecords() {

        return hasRole(RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can view their own call records
     */
    public boolean canViewOwnCallRecords() {
        return hasAnyRole(RoleConstants.CLIENT, RoleConstants.INTERPRETER, RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can create call records
     */
    public boolean canCreateCallRecords() {

        return hasAnyRole(RoleConstants.CLIENT, RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can manage translator profiles
     */
    public boolean canManageTranslatorProfiles() {
        return hasAnyRole(RoleConstants.INTERPRETER, RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can manage ratings
     */
    public boolean canManageRatings() {
        return hasAnyRole(RoleConstants.CLIENT, RoleConstants.INTERPRETER, RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can manage financial operations (deposits/withdrawals)
     */
    public boolean canManageFinancialOperations() {

        return hasAnyRole(RoleConstants.ADMINISTRATOR);
    }

    /**
     * Check if the current user can manage categories, languages, themes
     */
    public boolean canManageSystemResources() {

        return hasRole(RoleConstants.ADMINISTRATOR);
    }
}
