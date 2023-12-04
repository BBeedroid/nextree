package com.example.backend.controller;

import com.example.backend.dto.MemberDTO;
import com.example.backend.dto.ResponseDTO;
import com.example.backend.jwt.JwtTokenProvider;
import com.example.backend.service.MemberService;
import com.example.backend.util.InvalidEmailException;
import com.example.backend.util.MemberDuplicationException;
import com.example.backend.util.NoSuchMemberException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> createMember(@RequestBody MemberDTO memberDTO) {
        ResponseDTO<MemberDTO> responseDTO = new ResponseDTO<>();
        try {
            memberService.register(memberDTO);
            responseDTO.setItem(memberDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(responseDTO);
        } catch (MemberDuplicationException | InvalidEmailException e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/{memberEmail}")
    public ResponseEntity<?> searchByMemberEmail(@PathVariable("memberEmail") String memberEmail) {
        ResponseDTO<MemberDTO> responseDTO = new ResponseDTO<>();
        try {
            MemberDTO foundMemberDTO = memberService.find(memberEmail);
            responseDTO.setItem(foundMemberDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(responseDTO);
        } catch (NoSuchMemberException e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping
    public ResponseEntity<?> searchByNickname(@RequestParam("memberNickname") String memberNickname) {
        ResponseDTO<MemberDTO> responseDTO = new ResponseDTO<>();
        try {
            List<MemberDTO> foundMemberDTOs = memberService.findByNickname(memberNickname);
            responseDTO.setItems(foundMemberDTOs);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(responseDTO);
        } catch (NoSuchMemberException e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateMember(@RequestBody MemberDTO memberDTO,
                                          HttpServletRequest request) {
        ResponseDTO<MemberDTO> responseDTO = new ResponseDTO<>();
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long currentUserId = jwtTokenProvider.getMemberId(token);

            MemberDTO updatedMemberDTO = memberService.modify(currentUserId, memberDTO);
            responseDTO.setItem(updatedMemberDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(responseDTO);
        } catch (MemberDuplicationException | InvalidEmailException e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMember(HttpServletRequest request) {
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<>();
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Long currentUserId = jwtTokenProvider.getMemberId(token);

            memberService.remove(currentUserId);
            Map<String, String> returnMap = new HashMap<>();
            returnMap.put("message", "successfully removed member with id : " + currentUserId);
            responseDTO.setItem(returnMap);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (NoSuchMemberException e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
