package org.ersandev.nbazone.security.userdetail;

import lombok.RequiredArgsConstructor;
import org.ersandev.nbazone.user.User;
import org.ersandev.nbazone.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow( ()-> new UsernameNotFoundException("User Not Found with username: " + username));

        return  CustomUserDetails.build(user);

    }


}
