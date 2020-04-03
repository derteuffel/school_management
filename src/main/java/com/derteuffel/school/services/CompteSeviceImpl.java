package com.derteuffel.school.services;

import com.derteuffel.school.entities.*;
import com.derteuffel.school.enums.ERole;
import com.derteuffel.school.helpers.CompteRegistrationDto;
import com.derteuffel.school.repositories.CompteRepository;
import com.derteuffel.school.repositories.EcoleRepository;
import com.derteuffel.school.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by user on 22/03/2020.
 */
@Service
public class CompteSeviceImpl implements CompteService{

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EcoleRepository ecoleRepository;


    @Override
    public Compte findByUsername(String username) {
        return compteRepository.findByUsername(username);
    }

    @Override
    public Compte save(CompteRegistrationDto compteRegistrationDto, String s,Long id) {
        Compte compte = new Compte();

        Ecole ecole = ecoleRepository.getOne(id);
        compte.setEmail(compteRegistrationDto.getEmail());
        compte.setPassword(passwordEncoder.encode(compteRegistrationDto.getPassword()));
        compte.setUsername(compteRegistrationDto.getUsername());
        compte.setAvatar(s);
        compte.setEcole(ecole);

        Role role = new Role();

        if (compteRepository.findAll().size() <=1){
            role.setName(ERole.ROLE_ROOT.toString());
        }else {
            role.setName(ERole.ROLE_DIRECTEUR.toString());
        }

        Role existRole = roleRepository.findByName(role.getName());
        if (existRole != null){
            compte.setRoles(Arrays.asList(existRole));
        }else {
            roleRepository.save(role);
            compte.setRoles(Arrays.asList(role));
        }
        compteRepository.save(compte);
        return compte;
    }

    @Override
    public Compte saveEnseignant(CompteRegistrationDto compteRegistrationDto, String s, Long id, Enseignant enseignant) {
        Compte compte = new Compte();

        Ecole ecole = ecoleRepository.getOne(id);
        compte.setEmail(compteRegistrationDto.getEmail());
        compte.setPassword(passwordEncoder.encode(compteRegistrationDto.getPassword()));
        compte.setUsername(compteRegistrationDto.getUsername());
        compte.setAvatar(s);
        compte.setEcole(ecole);
        compte.setEnseignant(enseignant);

        Role role = new Role();


        Role existRole = roleRepository.findByName(ERole.ROLE_ENSEIGNANT.toString());
        if (existRole != null){
            compte.setRoles(Arrays.asList(existRole));
        }else {
            role.setName(ERole.ROLE_ENSEIGNANT.toString());
            roleRepository.save(role);
            compte.setRoles(Arrays.asList(role));
        }
        compteRepository.save(compte);
        return compte;
    }

    @Override
    public Compte saveParent(CompteRegistrationDto compteRegistrationDto, String s, Parent parent) {
        Compte compte = new Compte();

        compte.setEmail(compteRegistrationDto.getEmail());
        compte.setPassword(passwordEncoder.encode(compteRegistrationDto.getPassword()));
        compte.setUsername(compteRegistrationDto.getUsername());
        compte.setAvatar(s);
        compte.setParent(parent);
        Role role = new Role();


        Role existRole = roleRepository.findByName(ERole.ROLE_PARENT.toString());
        if (existRole != null){
            compte.setRoles(Arrays.asList(existRole));
        }else {
            role.setName(ERole.ROLE_PARENT.toString());
            roleRepository.save(role);
            compte.setRoles(Arrays.asList(role));
        }
        compteRepository.save(compte);
        return compte;
    }

    @Override
    public Compte saveEncadreur(CompteRegistrationDto compteRegistrationDto, String s, Encadreur encadreur) {
        Compte compte = new Compte();

        compte.setEmail(compteRegistrationDto.getEmail());
        compte.setPassword(passwordEncoder.encode(compteRegistrationDto.getPassword()));
        compte.setUsername(compteRegistrationDto.getUsername());
        compte.setAvatar(s);
        compte.setEnseignant(encadreur);
        Role role = new Role();


        Role existRole = roleRepository.findByName(ERole.ROLE_ENCADREUR.toString());
        if (existRole != null){
            compte.setRoles(Arrays.asList(existRole));
        }else {
            role.setName(ERole.ROLE_ENCADREUR.toString());
            roleRepository.save(role);
            compte.setRoles(Arrays.asList(role));
        }
        compteRepository.save(compte);
        return compte;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        Compte compte = compteRepository.findByUsername(username);
        if (compte == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(compte.getUsername(),
                compte.getPassword(),
                mapRolesToAuthorities(compte.getRoles()));
    }

    private Collection <? extends GrantedAuthority> mapRolesToAuthorities(Collection< Role > roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
