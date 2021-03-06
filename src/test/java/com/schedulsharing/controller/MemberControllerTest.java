package com.schedulsharing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedulsharing.config.RestDocsConfiguration;
import com.schedulsharing.dto.Club.ClubCreateRequest;
import com.schedulsharing.dto.member.*;
import com.schedulsharing.repository.MemberRepository;
import com.schedulsharing.service.ClubService;
import com.schedulsharing.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubService clubService;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
    }

    @DisplayName("???????????? ??????")
    @Test
    public void ????????????_??????() throws Exception {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("tester")
                .password("1234")
                .imagePath("imagePath")
                .build();

        mvc.perform(post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("imagePath").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("member-signup",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("password").description("????????????"),
                                fieldWithPath("name").description("???????????????????????? ????????? ??????"),
                                fieldWithPath("imagePath").description("???????????????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("??????????????? ????????? ?????? ?????????"),
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("name").description("???????????????????????? ????????? ??????"),
                                fieldWithPath("imagePath").description("???????????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("??????????????? ??? ???????????? ????????? ??????")
    @Test
    public void ??????????????????_????????????() throws Exception {
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("tester")
                .password("1234")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto1);

        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("tester2")
                .password("12345")
                .imagePath("imagePath2")
                .build();

        mvc.perform(post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequestDto2)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("duplicate").value(true));
    }

    @DisplayName("????????? ????????? ??????")
    @Test
    public void ????????????????????????() throws Exception {
        String email = "test@example.com";
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .email(email)
                .name("tester")
                .password("1234")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto1);
        EmailCheckRequestDto emailCheckRequestDto = new EmailCheckRequestDto(email);

        mvc.perform(post("/api/member/checkEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailCheckRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("duplicate").value(true))
                .andDo(document("member-checkEmail",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("???????????? ?????????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("duplicate").description("???????????? ?????????????????? true ???????????? ???????????? false"),
                                fieldWithPath("message").description("????????? ?????? ?????? ?????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("???????????? ????????? ??????????????????")
    @Test
    public void ??????????????????_????????????() throws Exception {
        String email = "test@example.com";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password("1234")
                .name("?????????")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto);

        createClub(email, "????????????", "???");
        createClub(email, "????????? ??????", "?????????");
        createClub(email, "?????? ??????", "??????");

        mvc.perform(get("/api/member/getClubs")
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.clubList[0].clubId").exists())
                .andExpect(jsonPath("_embedded.clubList[0].clubName").exists())
                .andExpect(jsonPath("_embedded.clubList[0].categories").exists())
                .andExpect(jsonPath("_embedded.clubList[0].leaderId").exists())
                .andDo(document("member-getClubs",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.clubList[0].clubId").description("????????? ?????? ?????????"),
                                fieldWithPath("_embedded.clubList[0].clubName").description("????????? ??????"),
                                fieldWithPath("_embedded.clubList[0].categories").description("????????? ????????????"),
                                fieldWithPath("_embedded.clubList[0].leaderId").description("????????? ????????? ????????? ???????????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("???????????? ???????????? ????????????")
    @Test
    public void ???????????????() throws Exception {
        String email = "test@example.com";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password("1234")
                .name("?????????")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto);
        String email2 = "test2@example.com";
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email(email2)
                .password("1234")
                .name("?????????")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto2);

        MemberSearchRequest memberSearchRequest = MemberSearchRequest.builder()
                .email("test2@example.com")
                .build();

        mvc.perform(get("/api/member/search")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberSearchRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("imagePath").exists())
                .andDo(document("member-findByEmail",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("email").description("????????? ????????? ?????????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("??????????????? ?????? ?????? ????????? ?????? ?????????"),
                                fieldWithPath("email").description("??????????????? ?????? ?????? ????????? ?????????"),
                                fieldWithPath("name").description("??????????????? ?????? ?????? ????????? ??????"),
                                fieldWithPath("imagePath").description("??????????????? ?????? ?????? ????????? ????????? ??????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("???????????? ????????? ?????? ????????? ?????? ??????")
    @Test
    public void ????????????_???????????????() throws Exception {
        String email = "test@example.com";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password("1234")
                .name("?????????")
                .imagePath("imagePath")
                .build();
        memberService.signup(signUpRequestDto);


        MemberSearchRequest memberSearchRequest = MemberSearchRequest.builder()
                .email("test3@example.com")
                .build();

        mvc.perform(get("/api/member/search")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberSearchRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("httpStatus").exists())
                .andExpect(jsonPath("error").exists())
                .andExpect(jsonPath("message").exists())
                .andDo(document("member-findByEmail-fail",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("email").description("????????? ????????? ?????????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("httpStatus").description("httpStatus"),
                                fieldWithPath("error").description("error ??????"),
                                fieldWithPath("message").description("?????? ???????????? ?????? ????????? ????????? ?????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("?????? id??? ?????? ??????")
    @Test
    public void ??????_id???_??????_??????() throws Exception {
        String email = "test@example.com";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password("1234")
                .name("?????????")
                .imagePath("imagePath")
                .build();
        SignUpResponseDto signUpResponseDto = memberService.signup(signUpRequestDto).getContent();
        mvc.perform(RestDocumentationRequestBuilders.get("/api/member/{id}", signUpResponseDto.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("imagePath").exists())
                .andDo(document("member-findById",
                        pathParameters(
                                parameterWithName("id").description("????????? ?????? ?????????")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("????????? ???????????????"),
                                fieldWithPath("email").description("????????? ????????? ??????"),
                                fieldWithPath("name").description("????????? ??????"),
                                fieldWithPath("imagePath").description("????????? ????????? ?????? ??????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("?????? ?????? ?????? ?????????")
    @Test
    public void ??????_??????_??????_?????????() throws Exception {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("?????????")
                .password("1234")
                .imagePath("imagePath")
                .build();
        SignUpResponseDto signUpResponseDto = memberService.signup(signUpRequestDto).getContent();
        MemberUpdateRequest memberUpdateRequest = MemberUpdateRequest.builder()
                .name("????????? ??????")
                .password("????????? ????????????")
                .imagePath("????????? ????????? ??????")
                .build();

        mvc.perform(RestDocumentationRequestBuilders.put("/api/member/{id}", signUpResponseDto.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("imagePath").exists())
                .andDo(document("member-update",
                        pathParameters(
                                parameterWithName("id").description("????????? ????????? ?????? ?????????")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("????????? ????????? ??????"),
                                fieldWithPath("password").description("????????? ????????? ????????????"),
                                fieldWithPath("imagePath").description("????????? ????????? ????????? ?????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("????????? ????????? ???????????????"),
                                fieldWithPath("name").description("????????? ????????? ??????"),
                                fieldWithPath("email").description("????????? ?????????"),
                                fieldWithPath("imagePath").description("????????? ????????? ????????? ?????? ??????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));

    }


    @DisplayName("?????? ?????? ??? ?????? ?????? ?????????")
    @Test
    public void ??????_??????_??????() throws Exception {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@example.com")
                .name("?????????")
                .password("1234")
                .imagePath("imagePath")
                .build();
        SignUpResponseDto signUpResponseDto = memberService.signup(signUpRequestDto).getContent();

        mvc.perform(RestDocumentationRequestBuilders.delete("/api/member/{id}", signUpResponseDto.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").exists())
                .andDo(document("member-delete",
                        pathParameters(
                                parameterWithName("id").description("????????? ????????? ?????? ?????????")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("success").description("????????? ???????????? ???"),
                                fieldWithPath("message").description("?????? ?????? message"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));


    }

    private void createClub(String email, String name, String categories) {
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(name)
                .categories(categories)
                .build();
        clubService.createClub(clubCreateRequest, email);
    }

    private String getBearToken() throws Exception {
        return "Bearer  " + getToken();
    }

    private String getToken() throws Exception {
        String email = "test@example.com";
        String password = "1234";

        LoginRequestDto loginDto = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        ResultActions perform = mvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));
        String responseBody = perform.andReturn().getResponse().getContentAsString();
        JacksonJsonParser parser = new JacksonJsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }
}