package com.example.task_manager.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
//import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.task_manager.DTO.AuthInfoDTO;
import com.example.task_manager.entity.Admin;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.repository.TeamMemberRepository;


@Service
public class AuthInfoService {

    protected final TeamMemberRepository teamMemberRepository;
    
    // Constructor for required repositories
    public AuthInfoService(TeamMemberRepository teamMemberRepository){
        this.teamMemberRepository = teamMemberRepository;
    }

    public boolean approveLogin(int teamMemberId, String enteredPassword) {
        boolean isSuccess = false;

        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + teamMemberId));

        String TMHashedPassword = teamMember.getAuthInfo().getHashedPassword();

        String TMSalt = teamMember.getAuthInfo().getSalt();
        String enteredHashedPassword = hashPassword(enteredPassword, TMSalt);

        if (enteredHashedPassword.equals(TMHashedPassword)) {
            isSuccess = true;
        }
        return isSuccess;
    }

    public AuthInfoDTO authenticateUser(int teamMemberId, String enteredPassword) {       
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + teamMemberId));

        if (!approveLogin(teamMemberId, enteredPassword)) {
            throw new RuntimeException("Invalid Credentials");
        }

        boolean isAdmin = isAdmin(teamMemberId);

        return new AuthInfoDTO(
            teamMember.getAccountId(),
            teamMember.getUserName(),
            isAdmin
        );
    }

    public boolean isAdmin(int teamMemberId) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + teamMemberId));

        return teamMember instanceof Admin;
    }

    public static String hashPassword(String plainTextPassword, String saltString){
        /*This method takes a user and the password that the user enters to log into their 
          account, and returns the password hash associated with that user and password. 
        */
    if (saltString.length() != 16 || saltString == null){
        throw new RuntimeException("Invalid salt entered");
    }
    byte[] salt = saltString.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
    int hashLength = 32;
 
    Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
      .withVersion(Argon2Parameters.ARGON2_VERSION_13)
      .withIterations(12) //number of iterations
      .withMemoryAsKB(6000) //uses this many kb or memory to hash
      .withParallelism(1) //only uses one thread to create the hash
      .withSalt(salt); //uses the salt we provide it
        
    Argon2BytesGenerator generate = new Argon2BytesGenerator();
    generate.init(builder.build());
    byte[] resultByteString = new byte[hashLength];
    generate.generateBytes(plainTextPassword.getBytes(StandardCharsets.UTF_8), resultByteString, 0, resultByteString.length);
    //generates an array of bytes as long as the hash length we specified
        
    String result = byteArrayToHexString(resultByteString);
    return result;
    }

    public static String byteArrayToHexString(byte[] byteArray){
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray){
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    public static String generateSalt(){
        /*
         * Uses the SecureRandom class to generate a cryptographically secure random 
         * string of bytes, which is returned as a string
         */
        SecureRandom secureRandom = new SecureRandom();
        int lengthOfSalt = 16;
        byte[] saltBytes = new byte[lengthOfSalt];
        secureRandom.nextBytes(saltBytes);
        String salt = new String(saltBytes, java.nio.charset.StandardCharsets.ISO_8859_1);
        //the charset above encodes 1 byte as 1 character, unlike utf_8
        return salt.toString();
    }
}
