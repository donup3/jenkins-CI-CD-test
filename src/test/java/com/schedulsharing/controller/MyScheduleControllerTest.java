package com.schedulsharing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedulsharing.config.RestDocsConfiguration;
import com.schedulsharing.dto.MySchedule.MyScheduleCreateRequest;
import com.schedulsharing.dto.MySchedule.MyScheduleCreateResponse;
import com.schedulsharing.dto.MySchedule.MyScheduleUpdateRequest;
import com.schedulsharing.dto.member.LoginRequestDto;
import com.schedulsharing.dto.member.SignUpRequestDto;
import com.schedulsharing.dto.yearMonth.YearMonthRequest;
import com.schedulsharing.entity.member.Member;
import com.schedulsharing.repository.MemberRepository;
import com.schedulsharing.repository.myschedule.MyScheduleRepository;
import com.schedulsharing.service.MemberService;
import com.schedulsharing.service.MyScheduleService;
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

import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class MyScheduleControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MyScheduleRepository myScheduleRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MyScheduleService myScheduleService;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
        myScheduleRepository.deleteAll();
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

        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .email("test2@example.com")
                .password("1234")
                .name("?????????")
                .imagePath("imagePath2")
                .build();

        memberService.signup(signUpRequestDto2);
    }

    @DisplayName("??? ????????? ??????")
    @Test
    public void ???_?????????_??????() throws Exception {
        MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                .name("??? ????????? ?????? ?????????")
                .contents("????????? ??????")
                .scheduleStartDate(LocalDateTime.now())
                .scheduleEndDate(LocalDateTime.now())
                .build();

        mvc.perform(post("/api/myschedule")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value("??? ????????? ?????? ?????????"))
                .andExpect(jsonPath("contents").value("????????? ??????"))
                .andExpect(jsonPath("scheduleStartDate").exists())
                .andExpect(jsonPath("scheduleEndDate").exists())
                .andDo(document("mySchedule-create",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("mySchedule-getOne").description("link to getOne"),
                                linkWithRel("mySchedule-update").description("link to update"),
                                linkWithRel("mySchedule-delete").description("link to delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("name").description("????????? ?????? ???????????? ?????? ?????? ??????"),
                                fieldWithPath("contents").description("????????? ?????? ???????????? ??????"),
                                fieldWithPath("scheduleStartDate").description("????????? ?????? ???????????? ?????? ??????"),
                                fieldWithPath("scheduleEndDate").description("????????? ?????? ???????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("myScheduleId").description("????????? ?????? ???????????? ???????????????"),
                                fieldWithPath("name").description("????????? ?????? ???????????? ?????? ?????? ??????"),
                                fieldWithPath("contents").description("????????? ?????? ???????????? ??????"),
                                fieldWithPath("scheduleStartDate").description("????????? ?????? ???????????? ?????? ??????"),
                                fieldWithPath("scheduleEndDate").description("????????? ?????? ???????????? ????????? ??????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.mySchedule-getOne.href").description("link to getOne"),
                                fieldWithPath("_links.mySchedule-update.href").description("link to update"),
                                fieldWithPath("_links.mySchedule-delete.href").description("link to delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));


    }

    @DisplayName("??? ????????? ?????? ??????")
    @Test
    public void ???_?????????_??????_??????() throws Exception {
        MyScheduleCreateResponse mySchedule = createMyScheduleByTest();
        mvc.perform(RestDocumentationRequestBuilders.get("/api/myschedule/{id}", mySchedule.getMyScheduleId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("myScheduleId").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("startDate").exists())
                .andExpect(jsonPath("endDate").exists())
                .andDo(document("mySchedule-getOne",
                        pathParameters(
                                parameterWithName("id").description("??? ???????????? ?????? ?????????")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("mySchedule-create").description("link to create"),
                                linkWithRel("mySchedule-update").description("link to update"),
                                linkWithRel("mySchedule-delete").description("link to delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("myScheduleId").description("????????? ?????? ???????????? ???????????????"),
                                fieldWithPath("name").description("????????? ?????? ???????????? ?????? ?????? ??????"),
                                fieldWithPath("contents").description("????????? ?????? ???????????? ??????"),
                                fieldWithPath("startDate").description("????????? ?????? ???????????? ?????? ??????"),
                                fieldWithPath("endDate").description("????????? ?????? ???????????? ????????? ??????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.mySchedule-create.href").description("link to create"),
                                fieldWithPath("_links.mySchedule-update.href").description("link to update"),
                                fieldWithPath("_links.mySchedule-delete.href").description("link to delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("???,?????? ???????????? ?????? ????????? ????????? ????????????")
    @Test
    public void ??????_?????????_???????????????() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();
        for (int i = 0; i < 3; i++) {
            MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                    .name("2021-2 ??? ????????? ?????? ?????????" + i)
                    .contents("2021-2 ??? ????????? ?????? ?????????" + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 2, 15, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .build();
            myScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        for (int i = 0; i < 5; i++) {
            MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                    .name("2021-3 ??? ????????? ?????? ?????????" + i)
                    .contents("2021-3 ??? ????????? ?????? ?????????" + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 3, 1, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 3, 2, 0, 0).plusDays(i))
                    .build();
            myScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        for (int i = 0; i < 3; i++) {
            MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                    .name("2021-4 ??? ????????? ?????? ?????????" + i)
                    .contents("2021-4 ??? ????????? ?????? ?????????" + i)
                    .scheduleStartDate(LocalDateTime.of(2021, 4, 1, 0, 0).plusDays(i))
                    .scheduleEndDate(LocalDateTime.of(2021, 4, 1, 0, 0).plusDays(i))
                    .build();
            myScheduleService.create(createRequest, member.getEmail()).getContent();
        }

        YearMonthRequest myYearMonthRequest = YearMonthRequest.builder()
                .yearMonth(YearMonth.of(2021, 3))
                .build();

        mvc.perform(RestDocumentationRequestBuilders.get("/api/myschedule/list")
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(myYearMonthRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.myScheduleResponseList[0].myScheduleId").exists())
                .andExpect(jsonPath("_embedded.myScheduleResponseList[0].name").exists())
                .andExpect(jsonPath("_embedded.myScheduleResponseList[0].contents").exists())
                .andExpect(jsonPath("_embedded.myScheduleResponseList[0].startDate").exists())
                .andExpect(jsonPath("_embedded.myScheduleResponseList[0].endDate").exists())
                .andDo(document("mySchedule-list",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("yearMonth").description("?????? ????????????????????? ????????? year,month")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.myScheduleResponseList[0].myScheduleId").description("????????? ?????? ????????????????????? ????????? ???????????? ???????????????"),
                                fieldWithPath("_embedded.myScheduleResponseList[0].name").description("????????? ?????? ????????????????????? ????????? ???????????? ??????"),
                                fieldWithPath("_embedded.myScheduleResponseList[0].contents").description("????????? ?????? ????????????????????? ????????? ???????????? ??????"),
                                fieldWithPath("_embedded.myScheduleResponseList[0].startDate").description("????????? ?????? ????????????????????? ????????? ???????????? ????????????"),
                                fieldWithPath("_embedded.myScheduleResponseList[0].endDate").description("????????? ?????? ????????????????????? ????????? ???????????? ?????????"),
                                fieldWithPath("_embedded.myScheduleResponseList[0]._links.mySchedule-create.href").description("link to create"),
                                fieldWithPath("_embedded.myScheduleResponseList[0]._links.mySchedule-getOne.href").description("link to getOne"),
                                fieldWithPath("_embedded.myScheduleResponseList[0]._links.mySchedule-update.href").description("link to update ???????????? ???????????? ????????????."),
                                fieldWithPath("_embedded.myScheduleResponseList[0]._links.mySchedule-delete.href").description("link to delete ???????????? ???????????? ????????????."),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("??? ????????? ?????? ??????")
    @Test
    public void ???_?????????_??????_??????() throws Exception {
        MyScheduleCreateResponse mySchedule = createMyScheduleByTest();
        MyScheduleUpdateRequest updateRequest = MyScheduleUpdateRequest.builder()
                .name("????????? ?????? ????????? ??????")
                .contents("????????? ?????? ????????? ??????")
                .scheduleStartDate(LocalDateTime.now().plusDays(1))
                .scheduleEndDate(LocalDateTime.now().plusDays(1))
                .build();

        mvc.perform(RestDocumentationRequestBuilders.put("/api/myschedule/{id}", mySchedule.getMyScheduleId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("myScheduleId").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("scheduleStartDate").exists())
                .andExpect(jsonPath("scheduleEndDate").exists())
                .andDo(document("mySchedule-update",
                        pathParameters(
                                parameterWithName("id").description("????????? ?????? ???????????? ?????? ?????????")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("mySchedule-create").description("link to create"),
                                linkWithRel("mySchedule-getOne").description("link to getOne"),
                                linkWithRel("mySchedule-delete").description("link to delete"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("???????????? ????????? ??????"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("????????? ?????? ???????????? ?????? ?????? ??????"),
                                fieldWithPath("contents").description("????????? ?????? ???????????? ??????"),
                                fieldWithPath("scheduleStartDate").description("????????? ?????? ???????????? ?????? ??????"),
                                fieldWithPath("scheduleEndDate").description("????????? ?????? ???????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("myScheduleId").description("????????? ?????? ???????????? ???????????????"),
                                fieldWithPath("name").description("????????? ?????? ???????????? ?????? ?????? ??????"),
                                fieldWithPath("contents").description("????????? ?????? ???????????? ??????"),
                                fieldWithPath("scheduleStartDate").description("????????? ?????? ???????????? ?????? ??????"),
                                fieldWithPath("scheduleEndDate").description("????????? ?????? ???????????? ????????? ??????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.mySchedule-create.href").description("link to create"),
                                fieldWithPath("_links.mySchedule-getOne.href").description("link to getOne"),
                                fieldWithPath("_links.mySchedule-delete.href").description("link to delete"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));

    }

    @DisplayName("?????? ????????? ?????? ??????")
    @Test
    public void ??????_?????????_??????_??????() throws Exception {
        MyScheduleCreateResponse createResponse = createMyScheduleByTest();
        mvc.perform(RestDocumentationRequestBuilders.delete("/api/myschedule/{id}", createResponse.getMyScheduleId())
                .header(HttpHeaders.AUTHORIZATION, getBearToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("message").exists())
                .andDo(document("mySchedule-delete",
                        pathParameters(
                                parameterWithName("id").description("????????? ?????? ???????????? ?????? ?????????")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("mySchedule-create").description("link to create"),
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
                                fieldWithPath("_links.mySchedule-create.href").description("link to create"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));

    }

    private MyScheduleCreateResponse createMyScheduleByTest() {
        String email = "test@example.com";
        String name = "?????? ????????? ?????? ?????????";
        String contents = "????????? ??????";
        LocalDateTime scheduleStartDate = LocalDateTime.now();
        LocalDateTime scheduleEndDate = LocalDateTime.now();

        MyScheduleCreateRequest createRequest = MyScheduleCreateRequest.builder()
                .name(name)
                .contents(contents)
                .scheduleStartDate(scheduleStartDate)
                .scheduleEndDate(scheduleEndDate)
                .build();

        return myScheduleService.create(createRequest, email).getContent();
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
