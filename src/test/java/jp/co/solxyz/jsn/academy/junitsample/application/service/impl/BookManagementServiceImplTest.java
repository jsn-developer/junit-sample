package jp.co.solxyz.jsn.academy.junitsample.application.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;

import jp.co.solxyz.jsn.academy.junitsample.infrastructure.api.component.BookOrderApi;
import jp.co.solxyz.jsn.academy.junitsample.infrastructure.api.request.BookOrderRequest;
import jp.co.solxyz.jsn.academy.junitsample.infrastructure.api.response.BookOrderResponse;
import jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.dto.BookManagementTableDto;
import jp.co.solxyz.jsn.academy.junitsample.infrastructure.domain.book.service.BookManagementTableService;

/**
 * BookManagementServiceImplのテスト
 * DBアクセスを行わない（モックを使用する）
 * 
 * @author JSN
 *
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class BookManagementServiceImplTest {

	@InjectMocks
	BookManagementServiceImpl sut;

	@Spy
	BookManagementTableService bookManagementTableService;

	@Mock
	BookOrderApi bookOrderApi;

	@Nested
	class Initのテスト {

		@Test
		@DisplayName("一覧取得成功_0件")
		void initSuccess0Records() throws Exception {
			List<BookManagementTableDto> result = new ArrayList<>();

			doReturn(result).when(bookManagementTableService).searchStockInfo();

			assertThat(sut.init()).isEqualTo(result).hasSize(0);
		}

		@Test
		@DisplayName("一覧取得成功_1件")
		void initSuccess1Record() throws Exception {
			List<BookManagementTableDto> result = new ArrayList<>();
			result.add(new BookManagementTableDto());

			doReturn(result).when(bookManagementTableService).searchStockInfo();

			assertThat(sut.init()).isEqualTo(result).hasSize(1);

		}

		@Test
		@DisplayName("一覧取得成功_2件")
		void initSuccess2Records() throws Exception {
			List<BookManagementTableDto> result = new ArrayList<>();
			result.add(new BookManagementTableDto());
			result.add(new BookManagementTableDto());

			doReturn(result).when(bookManagementTableService).searchStockInfo();

			assertThat(sut.init()).isEqualTo(result).hasSize(2);
		}

		@Test
		@DisplayName("一覧取得失敗")
		void initFailure() {
			doThrow(new DataAccessException("") {
			}).when(bookManagementTableService).searchStockInfo();
			assertThat(sut.init()).isNull();
		}
	}

	@Nested
	class Update {

		@Test
		@DisplayName("在庫情報更新成功")
		void updateSuccess() {
			BookManagementTableDto dto = new BookManagementTableDto();

			doReturn(dto).when(bookManagementTableService).updateStockInfo(any(BookManagementTableDto.class));

			assertThat(sut.update(0, "", 0)).isEqualTo(0);
		}

		@Test
		@DisplayName("在庫情報更新失敗")
		void updateFailure() {
			doThrow(new DataAccessException("") {
			}).when(bookManagementTableService).updateStockInfo(any(BookManagementTableDto.class));

			assertThat(sut.update(0, "", 0)).isEqualTo(1);
		}
	}

	@Nested
	class Order {

		@Test
		@DisplayName("発注成功_登録成功")
		void orderSuccessWithRegistrationSuccess() {
			BookOrderResponse res = new BookOrderResponse();
			BookManagementTableDto dto = new BookManagementTableDto();

			doReturn(res).when(bookOrderApi).order(any(BookOrderRequest.class));
			doReturn(dto).when(bookManagementTableService).updateStockInfo(any(BookManagementTableDto.class));

			assertThat(sut.order(0, "", 0)).isEqualTo(0);
		}

		@Test
		@DisplayName("発注成功_登録失敗")
		void orderSuccessWithRegistrationFailure() {
			BookOrderResponse res = new BookOrderResponse();

			doReturn(res).when(bookOrderApi).order(any(BookOrderRequest.class));
			doThrow(new DataAccessException("") {
			}).when(bookManagementTableService).updateStockInfo(any(BookManagementTableDto.class));

			assertThat(sut.order(0, "", 0)).isEqualTo(1);
		}

		@Test
		@DisplayName("発注失敗")
		void orderFailure() {
			doThrow(new RestClientException("") {
			}).when(bookOrderApi).order(any(BookOrderRequest.class));

			assertThat(sut.order(0, "", 0)).isEqualTo(1);
		}
	}

}
