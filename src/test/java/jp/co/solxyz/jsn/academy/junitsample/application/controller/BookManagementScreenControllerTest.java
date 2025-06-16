package jp.co.solxyz.jsn.academy.junitsample.application.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.dto.BookManagementTableDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.solxyz.jsn.academy.junitsample.application.service.BookManagementService;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(BookManagementScreenController.class)
class BookManagementScreenControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookManagementService bookManagementService;

	@Nested
	class Init {

		@Test
		void 書籍在庫情報リスト取得成功_0件() throws Exception {
			// Arrange
			List<BookManagementTableDto> emptyList = new ArrayList<>();
			when(bookManagementService.init()).thenReturn(emptyList);

			// Act & Assert
			mockMvc.perform(get("/manage/book"))
					.andExpect(status().isOk())
					.andExpect(view().name("bookmanager"))
					.andExpect(model().attribute("books", hasSize(0)));
		}

		@Test
		void 書籍在庫情報リスト取得成功_1件() throws Exception {
			// Arrange
			List<BookManagementTableDto> bookList = new ArrayList<>();

			BookManagementTableDto book = new BookManagementTableDto();
			bookList.add(book);

			when(bookManagementService.init()).thenReturn(bookList);

			// Act & Assert
			mockMvc.perform(get("/manage/book"))
					.andExpect(status().isOk())
					.andExpect(view().name("bookmanager"))
					.andExpect(model().attribute("books", hasSize(1)));
		}

		@Test
		void 書籍在庫情報リスト取得成功_2件() throws Exception {
			// Arrange
			List<BookManagementTableDto> bookList = new ArrayList<>();

			BookManagementTableDto book1 = new BookManagementTableDto();
			BookManagementTableDto book2 = new BookManagementTableDto();
			bookList.add(book2);
			bookList.add(book1);

			when(bookManagementService.init()).thenReturn(bookList);

			// Act & Assert
			mockMvc.perform(get("/manage/book"))
					.andExpect(status().isOk())
					.andExpect(view().name("bookmanager"))
					.andExpect(model().attribute("books", hasSize(2)));
		}

		@Test
		void 書籍在庫情報リスト取得失敗() throws Exception {
			// Arrange
			final String EXPECTED_MSG = "書籍情報がありません。";
			when(bookManagementService.init()).thenReturn(null);

			// Act & Assert
			mockMvc.perform(get("/manage/book"))
					.andExpect(status().isOk())
					.andExpect(view().name("bookmanager"))
					.andExpect(model().attribute("message", EXPECTED_MSG));
		}
	}

	@Nested
	class Update {

		@Test
		void 書籍在庫情報リスト更新成功() throws Exception {
			// Arrange
			final String EXPECTED_MSG = "正常に更新されました。";
			when(bookManagementService.update(5, "test", 100)).thenReturn(0);

			// Act & Assert
			mockMvc.perform(post("/manage/book")
							.param("update", "")
							.param("bookId", "5")
							.param("bookName", "test")
							.param("stock", "100"))
					.andExpect(status().isOk())
					.andExpect(view().name("bookmanager"))
					.andExpect(model().attribute("message", EXPECTED_MSG));

			verify(bookManagementService).update(5, "test", 100);
		}

		@Test
		void 書籍在庫情報リスト取得失敗() throws Exception {
			// Arrange
			final String EXPECTED_MSG = "更新に失敗しました。";
			when(bookManagementService.update(5, "test", 100)).thenReturn(1);

			// Act & Assert
			mockMvc.perform(post("/manage/book")
							.param("update", "")
							.param("bookId", "5")
							.param("bookName", "test")
							.param("stock", "100"))
					.andExpect(status().isOk())
					.andExpect(view().name("bookmanager"))
					.andExpect(model().attribute("message", EXPECTED_MSG));

			verify(bookManagementService).update(5, "test", 100);
		}
	}

	@Nested
	class Order {

		@Test
		void 書籍在庫情報リスト更新成功() throws Exception {
			// Arrange
			final String EXPECTED_MSG = "正常に発注されました。";
			when(bookManagementService.order(5, "test", 100)).thenReturn(0);

			// Act & Assert
			mockMvc.perform(post("/manage/book")
							.param("order", "")
							.param("bookId", "5")
							.param("bookName", "test")
							.param("stock", "100"))
					.andExpect(status().isOk())
					.andExpect(view().name("bookmanager"))
					.andExpect(model().attribute("message", EXPECTED_MSG));

			verify(bookManagementService).order(5, "test", 100);
		}

		@Test
		void 書籍在庫情報リスト取得失敗() throws Exception {
			// Arrange
			final String EXPECTED_MSG = "発注に失敗しました。";
			when(bookManagementService.order(5, "test", 100)).thenReturn(1);

			// Act & Assert
			mockMvc.perform(post("/manage/book")
							.param("order", "")
							.param("bookId", "5")
							.param("bookName", "test")
							.param("stock", "100"))
					.andExpect(status().isOk())
					.andExpect(view().name("bookmanager"))
					.andExpect(model().attribute("message", EXPECTED_MSG));

			verify(bookManagementService).order(5, "test", 100);
		}
	}
}
