package com.schedulsharing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedulsharing.config.RestDocsConfiguration;
import com.schedulsharing.dto.Club.ClubCreateRequest;
import com.schedulsharing.dto.Club.ClubCreateResponse;
import com.schedulsharing.dto.Club.ClubInviteRequest;
import com.schedulsharing.dto.Club.ClubUpdateRequest;
import com.schedulsharing.dto.member.LoginRequestDto;
import com.schedulsharing.dto.member.SignUpRequestDto;
import com.schedulsharing.dto.member.SignUpResponseDto;
import com.schedulsharing.repository.ClubRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class ClubControllerTest {
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
    @Autowired
    private ClubRepository clubRepository;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        clubRepository.deleteAll();
        String email = "test@example.com";
        String password = "1234";
        String imagePath = "imagePath";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email(email)
                .password(password)
                .name("?????????")
                .imagePath(imagePath)
                .build();

        memberService.signup(signUpRequestDto);
    }

    @DisplayName("?????? ??????????????? ????????????")
    @Test
    public void ????????????_????????????() throws Exception {
        String clubName = "????????????";
        String categories = "???";
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName(clubName)
                .categories(categories)
                .build();

        mvc.perform(post("/api/club")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("clubId").exists())
                .andExpect(jsonPath("clubName").exists())
                .andExpect(jsonPath("categories").exists())
                .andExpect(jsonPath("leaderId").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("club-create",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("club-invite").description("link to club-invite"),
                                linkWithRel("club-getOne").description("link to club-getOne"),
                                linkWithRel("club-update").description("link to club-update"),
                                linkWithRel("club-delete").description("link to club-delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("clubName").description("????????? ????????? ??????"),
                                fieldWithPath("categories").description("????????? ????????? ????????????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("clubId").description("????????? ????????? ?????? ?????????"),
                                fieldWithPath("clubName").description("????????? ????????? ??????"),
                                fieldWithPath("categories").description("????????? ????????? ????????????"),
                                fieldWithPath("leaderId").description("????????? ?????? ????????? ?????? ?????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.club-invite.href").description("link to club-invite"),
                                fieldWithPath("_links.club-getOne.href").description("link to club-getOne"),
                                fieldWithPath("_links.club-update.href").description("link to club-update"),
                                fieldWithPath("_links.club-delete.href").description("link to club-delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("????????? ??????????????? ????????????")
    @Test
    public void ??????_????????????() throws Exception {
        //given
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("12345")
                .name("?????????2")
                .imagePath("imagePath10")
                .build();
        SignUpResponseDto signUpResponseDto1 = memberService.signup(signUpRequestDto1).getContent();
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email("test3@example.com")
                .password("123456")
                .name("?????????3")
                .imagePath("imagePath101")
                .build();
        SignUpResponseDto signUpResponseDto2 = memberService.signup(signUpRequestDto2).getContent();

        Long member1Id = signUpResponseDto1.getId();
        Long member2Id = signUpResponseDto2.getId();

        Long clubId = createClub("test@example.com");

        ClubInviteRequest clubInviteRequest = ClubInviteRequest.builder()
                .memberIds(List.of(member1Id, member2Id))
                .build();
        //when
        mvc.perform(RestDocumentationRequestBuilders.post("/api/club/{clubId}/invite", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubInviteRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("club-invite",
                        pathParameters(
                                parameterWithName("clubId").description("????????? ????????? ?????? ?????????")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("club-create").description("link to club-create"),
                                linkWithRel("club-update").description("link to club-update"),
                                linkWithRel("club-getOne").description("link to club-getOne"),
                                linkWithRel("club-delete").description("link to club-delete")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("memberIds").description("????????? ???????????? ????????? ????????? ?????????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("success").description("????????? ??????????????? true ????????? ????????? false"),
                                fieldWithPath("message").description("????????? ?????????????????? ?????? ?????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.club-create.href").description("link to club-create"),
                                fieldWithPath("_links.club-update.href").description("link to club-update"),
                                fieldWithPath("_links.club-getOne.href").description("link to club-getOne"),
                                fieldWithPath("_links.club-delete.href").description("link to club-delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("????????? ??????????????? ?????? ??????????????? ????????? ?????? ??????")
    @Test
    public void ???????????????????????????????????????_InvalidGrantException() throws Exception {
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("12345")
                .name("?????????2")
                .imagePath("imagePath10")
                .build();
        SignUpResponseDto signUpResponseDto1 = memberService.signup(signUpRequestDto1).getContent();
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email("test3@example.com")
                .password("123456")
                .name("?????????3")
                .imagePath("imagePath101")
                .build();
        SignUpResponseDto signUpResponseDto2 = memberService.signup(signUpRequestDto2).getContent();

        Long member1Id = signUpResponseDto1.getId();
        Long member2Id = signUpResponseDto2.getId();

        Long clubId = createClub("test2@example.com");

        ClubInviteRequest clubInviteRequest = ClubInviteRequest.builder()
                .memberIds(List.of(member1Id, member2Id))
                .build();
        //when
        mvc.perform(post("/api/club/{clubId}/invite", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubInviteRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("success").value(false));
    }

    @DisplayName("???????????? ?????? ?????? ???????????? ???????????? ????????? ???????????????.")
    @Test
    public void ?????????_????????????() throws Exception {
        Long clubId = createClub("test@example.com");
        mvc.perform(RestDocumentationRequestBuilders.get("/api/club/{clubId}", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("clubId").exists())
                .andExpect(jsonPath("clubName").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.club-create.href").exists())
                .andExpect(jsonPath("_links.club-invite.href").exists())
                .andExpect(jsonPath("_links.club-update.href").exists())
                .andExpect(jsonPath("_links.club-delete.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("club-getOne",
                        pathParameters(
                                parameterWithName("clubId").description("????????? ????????? ?????? ?????????")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("club-create").description("link to club-create"),
                                linkWithRel("club-invite").description("link to club-invite"),
                                linkWithRel("club-update").description("link to club-invite"),
                                linkWithRel("club-delete").description("link to club-delete")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("clubId").description("????????? ????????? ???????????????"),
                                fieldWithPath("clubName").description("????????? ????????? ??????"),
                                fieldWithPath("categories").description("????????? ????????? ????????????"),
                                fieldWithPath("leaderId").description("????????? ????????? ?????? ????????? ???????????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.club-create.href").description("link to club-create"),
                                fieldWithPath("_links.club-update.href").description("link to club-update, ???????????????????????? ????????????."),
                                fieldWithPath("_links.club-invite.href").description("link to club-invite, ???????????????????????? ????????????."),
                                fieldWithPath("_links.club-delete.href").description("link to club-delete, ???????????????????????? ????????????."),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("???????????? ?????? ?????? ?????? ???????????? ???????????? ??????????????????.")
    @Test
    public void ????????????_????????????_????????????() throws Exception {
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("12345")
                .name("?????????2")
                .imagePath("imagePath10")
                .build();
        memberService.signup(signUpRequestDto1);
        Long clubId = createClub("test2@example.com");
        mvc.perform(RestDocumentationRequestBuilders.get("/api/club/{clubId}", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("clubId").exists())
                .andExpect(jsonPath("clubName").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.club-create.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists());
    }

    @DisplayName("????????? ????????? ?????? ????????? ??????")
    @Test
    public void ?????????_??????_??????() throws Exception {
        mvc.perform(RestDocumentationRequestBuilders.get("/api/club/11111")
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("httpStatus").exists())
                .andExpect(jsonPath("error").exists())
                .andExpect(jsonPath("message").exists());
    }

    @DisplayName("?????? ?????? ??????")
    @Test
    public void ??????????????????() throws Exception {
        Long clubId = createClub("test@example.com");
        ClubUpdateRequest clubUpdateRequest = ClubUpdateRequest.builder()
                .clubName("????????? ????????????")
                .categories("????????? ??????????????????")
                .build();
        mvc.perform(RestDocumentationRequestBuilders.put("/api/club/{clubId}", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clubUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("clubId").exists())
                .andExpect(jsonPath("clubName").exists())
                .andExpect(jsonPath("categories").exists())
                .andExpect(jsonPath("leaderId").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.club-create.href").exists())
                .andExpect(jsonPath("_links.club-invite.href").exists())
                .andExpect(jsonPath("_links.club-delete.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("club-update",
                        pathParameters(
                                parameterWithName("clubId").description("????????? ????????? ?????? ?????????")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("club-create").description("link to club-create"),
                                linkWithRel("club-getOne").description("link to club-getOne"),
                                linkWithRel("club-invite").description("link to club-invite"),
                                linkWithRel("club-delete").description("link to club-delete")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("clubId").description("????????? ????????? ?????? ?????????"),
                                fieldWithPath("clubName").description("????????? ????????? ??????"),
                                fieldWithPath("categories").description("????????? ????????? ????????????"),
                                fieldWithPath("leaderId").description("????????? ?????? ????????? ?????? ?????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.club-create.href").description("link to club-create"),
                                fieldWithPath("_links.club-delete.href").description("link to club-delete"),
                                fieldWithPath("_links.club-getOne.href").description("link to club-getOne"),
                                fieldWithPath("_links.club-invite.href").description("link to club-invite"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("?????? ?????? ??????")
    @Test
    public void ??????????????????() throws Exception {
        Long clubId = createClub("test@example.com");

        mvc.perform(RestDocumentationRequestBuilders.delete("/api/club/{clubId}", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.club-create.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andDo(document("club-delete",
                        pathParameters(
                                parameterWithName("clubId").description("????????? ????????? ?????? ?????????")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("club-create").description("link to club-create")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("success").description("????????? ??????????????? true ????????? ????????? false"),
                                fieldWithPath("message").description("????????? ?????????????????? ?????? ?????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.club-create.href").description("link to club-create"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }


    @DisplayName("????????? ?????? ?????? ?????? ?????? ??????")
    @Test
    public void ??????????????????() throws Exception {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("12345")
                .name("?????????2")
                .imagePath("imagePath10")
                .build();
        memberService.signup(signUpRequestDto).getContent();

        Long clubId = createClub("test2@example.com");

        mvc.perform(delete("/api/club/{clubId}", clubId)
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private Long createClub(String email) {
        ClubCreateRequest clubCreateRequest = ClubCreateRequest.builder()
                .clubName("????????????")
                .categories("???")
                .build();
        ClubCreateResponse clubCreateResponse = clubService.createClub(clubCreateRequest, email).getContent();
        return clubCreateResponse.getClubId();
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