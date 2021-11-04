package xyz.fivemillion.tdd.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xyz.fivemillion.tdd.domain.MembershipType;
import xyz.fivemillion.tdd.dto.MembershipAddResponse;
import xyz.fivemillion.tdd.dto.MembershipDetailResponse;
import xyz.fivemillion.tdd.dto.MembershipRequest;
import xyz.fivemillion.tdd.error.MembershipError;
import xyz.fivemillion.tdd.exception.MembershipException;
import xyz.fivemillion.tdd.service.MembershipService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static xyz.fivemillion.tdd.controller.MembershipConstants.USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
public class MembershipControllerTest {

    @Mock
    private MembershipService membershipService;

    @InjectMocks
    private MembershipController membershipController;

    private MockMvc mvc;
    private Gson gson;

    @BeforeEach
    public void init() {
        gson = new Gson();
        mvc = MockMvcBuilders.standaloneSetup(membershipController).build();
    }

    @Test
    public void 맴버쉽등록실패_userId_is_empty() throws Exception {
        //given
        String url = "/api/v1/membership";

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .content(gson.toJson(buildMembershipRequest(10000, MembershipType.NAVER))
                        ).contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    private MembershipRequest buildMembershipRequest(Integer point, MembershipType membershipType) {
        return MembershipRequest.builder()
                .point(point)
                .membershipType(membershipType)
                .build();
    }

    @Test
    public void 맴버십등록실패_point_is_null() throws Exception {
        //given
        String url = "/api/v1/membership";

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(buildMembershipRequest(null, MembershipType.NAVER))
                        ).contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 맴버십등록실패_point_is_negative() throws Exception {
        //given
        String url = "/api/v1/membership";

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(buildMembershipRequest(-1, MembershipType.NAVER))
                        ).contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 맴버십등록실패_membershipType_is_null() throws Exception {
        //given
        String url = "/api/v1/membership";

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(buildMembershipRequest(10000, null))
                        ).contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 맴버십등록실패_이미_존재하는_맴버십() throws Exception {
        //given
        String url = "/api/v1/membership";
        given(
                membershipService.addMembership("12345", MembershipType.NAVER, 10000)
        ).willThrow(new MembershipException(MembershipError.DUPLICATED_MEMBERSHIP_REGISTER));

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(buildMembershipRequest(10000, MembershipType.NAVER))
                        ).contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 맴버십등록성공() throws Exception {
        //given
        String url = "/api/v1/membership";
        MembershipAddResponse response = MembershipAddResponse.builder()
                .id(-1L)
                .membershipType(MembershipType.NAVER)
                .build();

        given(
                membershipService.addMembership("12345", MembershipType.NAVER, 10000)
        ).willReturn(response);

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(buildMembershipRequest(10000, MembershipType.NAVER))
                        ).contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isCreated());

        final MembershipAddResponse resultResponse = gson.fromJson(
                result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MembershipAddResponse.class);

        assertNotNull(resultResponse.getId());
        assertEquals(response.getMembershipType(), resultResponse.getMembershipType());
    }

    @Test
    public void 맴버십조회실패_사용자식별값헤더에없음() throws Exception {
        //given
        String url = "/api/v1/membership/list";

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get(url));

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 맴버십조회성공() throws Exception {
        //given
        String url = "/api/v1/membership/list";
        given(membershipService.getMembershipList("12345"))
                .willReturn(
                        Arrays.asList(
                                MembershipDetailResponse.builder().build(),
                                MembershipDetailResponse.builder().build(),
                                MembershipDetailResponse.builder().build()
                        )
                );

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .get(url)
                        .header(USER_ID_HEADER, "12345")
        );

        //then
        result.andExpect(status().isOk());
    }

    @Test
    public void 맴버십상세조회실패_사용자식별자없음() throws Exception {
        //given
        String url = "/api/v1/membership/detail";

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.get(url));

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 맴버십상세조회실패_맴버십타입이파라미터에없음() throws Exception {
        //given
        String url = "/api/v1/membership/detail";

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .get(url)
                        .header(USER_ID_HEADER, "12345")
                        .param("membershipType", "empty")
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 맴버십상세조회실패_결과없음() throws Exception {
        //given
        String url = "/api/v1/membership/detail";
        given(membershipService.getMembership("12345", MembershipType.NAVER))
                .willThrow(new MembershipException(MembershipError.MEMBERSHIP_NOT_FOUND));

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .get(url)
                        .header(USER_ID_HEADER, "12345")
                        .param("membershipType", MembershipType.NAVER.name())
        );

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    public void 맴버십상세조회성공() throws Exception {
        //given
        String url = "/api/v1/membership/detail";
        given(membershipService.getMembership("12345", MembershipType.NAVER)).willReturn(
                MembershipDetailResponse.builder().build()
        );

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .get(url)
                        .header(USER_ID_HEADER, "12345")
                        .param("membershipType", MembershipType.NAVER.name())
        );

        //then

        result.andExpect(status().isOk());
    }

    @Test
    public void 맴버십삭제실패_사용자식별자가헤더에없음() throws Exception {
        //given
        String url = "/api/v1/membership/-1";

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .delete(url)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 맴버십삭제성공() throws Exception {
        //given
        String url = "/api/v1/membership/-1";

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .delete(url)
                        .header(USER_ID_HEADER, "12345")
        );

        //then
        result.andExpect(status().isNoContent());
    }

    @Test
    public void 포인트적립실패_헤더에사용자식별자가없음() throws Exception {
        //given
        String url = "/api/v1/membership/-1/accumulate";

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .content(gson.toJson(buildMembershipRequest(10000)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 포인트적립실패_포인트가음수() throws Exception {
        //given
        String url = "/api/v1/membership/-1/accumulate";

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(buildMembershipRequest(-1)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void 포인트적립성공() throws Exception {
        //given
        String url = "/api/v1/membership/-1/accumulate";

        //when
        ResultActions result = mvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(buildMembershipRequest(10000)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk());
    }

    private MembershipRequest buildMembershipRequest(int point) {
        return MembershipRequest.builder().point(point).build();
    }
}
