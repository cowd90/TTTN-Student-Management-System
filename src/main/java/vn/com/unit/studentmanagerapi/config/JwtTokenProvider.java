package vn.com.unit.studentmanagerapi.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.*;

@Slf4j
@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtTokenProvider {
    GlobalValue config;
    UserDetailsService userDetailsService;

    public String createToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        Set<String> roles = new HashSet<>();
        for(GrantedAuthority i : userDetails.getAuthorities()){
            roles.add(i.getAuthority());
        }
        claims.put("role", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+config.getExpiration().toMillis()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        Claims body = createClaim(token);

        Date expiration = body.getExpiration();
        if(expiration.before(new Date())) return false;

        String subject = body.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);


        return userDetails != null;
    }

    public Authentication getAuthentication(String token) {
        String subject = createClaim(token).getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

        if(userDetails==null)
            throw new UsernameNotFoundException("User not found");

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request){
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if( header== null || !header.startsWith("Bearer "))
            throw new JwtException("Miss Token");

        return header.substring(7);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(config.getSignerKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims createClaim(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
