package jp.co.solxyz.jsn.academy.junitsample.infrastructure.domain.book.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.dto.BookManagementTableDto;
import jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.repository.BookManagementTableRepository;

/**
 * BookManagementTableServiceImplのテスト
 * BookManagementTableRepositoryをモック化する
 *
 * @author JSN
 *
 */
@ExtendWith(MockitoExtension.class)
class BookManagementTableServiceImplTest {

	@InjectMocks
	BookManagementTableServiceImpl bookManagementTableService;

	@Mock
	BookManagementTableRepository bookManagementTableRepository;

	@Nested
	class SearchStockInfo {

		@Test
		@DisplayName("書籍在庫情報リスト取得成功_2件")
		void searchStockInfoSuccess2Records() {
			List<BookManagementTableDto> mockList = new ArrayList<>();
			mockList.add(new BookManagementTableDto());
			mockList.add(new BookManagementTableDto());

			doReturn(mockList).when(bookManagementTableRepository).findAll();

			var actual = bookManagementTableService.searchStockInfo();
			assertThat(actual).hasSize(2);
		}

		@Test
		@DisplayName("書籍在庫情報リスト取得成功_1件")
		void searchStockInfoSuccess1Record() {
			List<BookManagementTableDto> mockList = new ArrayList<>();
			mockList.add(new BookManagementTableDto());

			doReturn(mockList).when(bookManagementTableRepository).findAll();

			var actual = bookManagementTableService.searchStockInfo();
			assertThat(actual).hasSize(1);
		}

		@Test
		@DisplayName("書籍在庫情報リスト取得成功_0件")
		void searchStockInfoSuccess0Records() {
			List<BookManagementTableDto> emptyList = new ArrayList<>();

			doReturn(emptyList).when(bookManagementTableRepository).findAll();

			var actual = bookManagementTableService.searchStockInfo();
			assertThat(actual).hasSize(0);
		}

		@Test
		@DisplayName("書籍在庫情報リスト取得失敗")
		void searchStockInfoFailure() {
			doThrow(new DataIntegrityViolationException("DB Error")).when(bookManagementTableRepository).findAll();

			assertThrows(DataIntegrityViolationException.class, () -> {
				bookManagementTableService.searchStockInfo();
			});
		}
	}

	@Nested
	class UpdateStockInfo {

		@Test
		@DisplayName("書籍在庫情報更新成功")
		void updateStockInfoSuccess() {
			BookManagementTableDto dto = new BookManagementTableDto();

			doReturn(dto).when(bookManagementTableRepository).saveAndFlush(dto);

			var actualDto = bookManagementTableService.updateStockInfo(dto);
			assertThat(actualDto).isEqualTo(dto);
		}

		@Test
		@DisplayName("書籍在庫情報更新失敗")
		void updateStockInfoFailure() {
			BookManagementTableDto dto = new BookManagementTableDto();

			doThrow(new DataIntegrityViolationException("DB Error")).when(bookManagementTableRepository).saveAndFlush(dto);

			assertThrows(DataIntegrityViolationException.class, () -> {
				bookManagementTableService.updateStockInfo(dto);
			});
		}
	}
}
