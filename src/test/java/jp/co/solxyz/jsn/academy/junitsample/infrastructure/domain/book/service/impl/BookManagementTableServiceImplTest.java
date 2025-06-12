package jp.co.solxyz.jsn.academy.junitsample.infrastructure.domain.book.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

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
	BookManagementTableServiceImpl sut;

	@Mock
	BookManagementTableRepository bookManagementTableRepository;

	@Nested
	class SearchStockInfo {

		@Test
		void 書籍在庫情報リスト取得成功_2件() {
			List<BookManagementTableDto> mockList = new ArrayList<>();
			mockList.add(new BookManagementTableDto());
			mockList.add(new BookManagementTableDto());

			doReturn(mockList).when(bookManagementTableRepository).findAll();

			var actual = sut.searchStockInfo();
			assertThat(actual).hasSize(2);
		}

		@Test
		void 書籍在庫情報リスト取得成功_1件() {
			List<BookManagementTableDto> mockList = new ArrayList<>();
			mockList.add(new BookManagementTableDto());

			doReturn(mockList).when(bookManagementTableRepository).findAll();

			var actual = sut.searchStockInfo();
			assertThat(actual).hasSize(1);
		}

		@Test
		void 書籍在庫情報リスト取得成功_0件() {
			List<BookManagementTableDto> emptyList = new ArrayList<>();

			doReturn(emptyList).when(bookManagementTableRepository).findAll();

			var actual = sut.searchStockInfo();
			assertThat(actual).hasSize(0);
		}

		@Test
		void 書籍在庫情報リスト取得失敗() {
			doThrow(new DataIntegrityViolationException("DB Error")).when(bookManagementTableRepository).findAll();

			assertThrows(DataIntegrityViolationException.class, () -> {
				sut.searchStockInfo();
			});
		}
	}

	@Nested
	class UpdateStockInfo {

		@Test
		void 書籍在庫情報更新成功() {
			BookManagementTableDto dto = new BookManagementTableDto();

			doReturn(dto).when(bookManagementTableRepository).saveAndFlush(dto);

			var actualDto = sut.updateStockInfo(dto);
			assertThat(actualDto).isEqualTo(dto);
		}

		@Test
		void 書籍在庫情報更新失敗() {
			BookManagementTableDto dto = new BookManagementTableDto();

			doThrow(new DataIntegrityViolationException("DB Error")).when(bookManagementTableRepository).saveAndFlush(dto);

			assertThrows(DataIntegrityViolationException.class, () -> {
				sut.updateStockInfo(dto);
			});
		}
	}
}
