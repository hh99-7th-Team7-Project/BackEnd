//package com.sparta.coffang;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sparta.coffang.dto.requestDto.PostRequestDto;
//import com.sparta.coffang.dto.requestDto.SignupRequestDto;
//import com.sparta.coffang.model.Post;
//import com.sparta.coffang.model.User;
//import com.sparta.coffang.repository.PostRepository;
//import com.sparta.coffang.security.UserDetailsImpl;
//import com.sparta.coffang.service.PostService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class PostServiceTest {
//    @Mock
//    PostRepository postRepository;
//    @Mock
//    User user;
//    @Mock
//    UserDetailsImpl userDetails;
//
//    @Nested
//    @DisplayName("게시물 생성")
//    class CreateArticle {
//        private PostRequestDto postRequestDto1 = PostRequestDto.builder()
//                .title("testTitle")
//                .category("testCategory")
//                .content("testContent")
//                .build();
//
//        @Nested
//        @DisplayName("정상 케이스")
//        class SuccessCase {
//            @Test
//            @DisplayName("새로운 게시물 생성")
//            void createArticleSuccess1() {
//                Post post = Post.builder()
//                        .title(postRequestDto1.getTitle())
//                        .content(postRequestDto1.getContent())
//                        .category(postRequestDto1.getCategory())
//                        .createdAt(LocalDateTime.now())
//                        .user(user)
//                        .build();
//
//                when(postRepository.save(any(Post.class))).thenReturn(post);
//
//                PostService postService = new PostService(postRepository);
//                Post result = postService.savaPost(postRequestDto1, userDetails).getBody();
//
//                assertThat(result.getContent()).isEqualTo("testContent");
//            }
//        }
//
//
//        @Nested
//        @DisplayName("비정상 케이스")
//        class BadCase {
//            @Test
//            @DisplayName("반환된 게시물이 NULL인 경우")
//            void createArticleSuccess1() {
//                Post post = Post.builder()
//                        .title(postRequestDto1.getTitle())
//                        .content(postRequestDto1.getContent())
//                        .category(postRequestDto1.getCategory())
//                        .createdAt(LocalDateTime.now())
//                        .user(user)
//                        .build();
//
//                when(postRepository.save(any(Post.class))).thenReturn(null);
//
//                PostService postService = new PostService(postRepository);
//                Post result = postService.savaPost(postRequestDto1, userDetails).getBody();
//
//                assertThat(result).isNull();
//            }
//        }
//    }
//}
