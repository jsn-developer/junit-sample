package jp.co.solxyz.jsn.academy.junitsample.application.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Connection;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.solxyz.jsn.academy.junitsample.application.form.BookManagementScreenForm;
import jp.co.solxyz.jsn.academy.junitsample.application.service.BookManagementService;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class BookManagementScreenControllerTest {

	@Autowired
	BookManagementScreenController sut;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private MockMvc mockMvc;

	@Spy
	BookManagementService bookManagementService;

	@AfterEach
	public void after() throws Exception {

		// DBコネクション取得
		Connection conn;

		conn = jdbcTemplate.getDataSource().getConnection();
		IDatabaseConnection dbconn = new DatabaseConnection(conn);

		QueryDataSet dataSet = new QueryDataSet(dbconn);
		// retrieve all rows from specified table
		dataSet.addTable("BOOK_MANAGEMENT_TBL");

		DatabaseOperation.DELETE_ALL.execute(dbconn, dataSet);

	}

	@Nested
	class Init {

		@Test
		@Sql(statements = {
				"DELETE BOOK_MANAGEMENT_TBL",
		})
		void 書籍在庫情報リスト取得成功_0件() {
			try {
				mockMvc.perform(get("/manage/book"))
						.andExpect(status().isOk())
						.andExpect(view().name(is("bookmanager")))
						.andExpect(model().attribute("books", hasSize(0)));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Test
		@Sql(statements = {
				"DELETE BOOK_MANAGEMENT_TBL",
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)"
		})
		void 書籍在庫情報リスト取得成功_1件() {
			try {
				mockMvc.perform(get("/manage/book"))
						.andExpect(status().isOk())
						.andExpect(view().name(is("bookmanager")))
						.andExpect(model().attribute("books", hasSize(1)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Test
		@Sql(statements = {
				"DELETE BOOK_MANAGEMENT_TBL",
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)",
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, 'JUnit詳解', 200, 3)"
		})
		void 書籍在庫情報リスト取得成功_2件() {
			try {
				mockMvc.perform(get("/manage/book"))
						.andExpect(status().isOk())
						.andExpect(view().name(is("bookmanager")))
						.andExpect(model().attribute("books", hasSize(2)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Test
		void 書籍在庫情報リスト取得失敗() {
			final String EXPECTED_MSG = "書籍情報がありません。";

			Whitebox.setInternalState(sut, BookManagementService.class, bookManagementService);
			doReturn(null).when(bookManagementService).init();

			try {
				mockMvc.perform(get("/manage/book"))
						.andExpect(status().isOk())
						.andExpect(view().name(is("bookmanager")))
						.andExpect(model().attribute("message", EXPECTED_MSG));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Nested
	class UpdateBookInfo {
		@Test
		@Sql(statements = {
				"DELETE BOOK_MANAGEMENT_TBL",
		})
		void 書籍在庫情報リスト更新成功() {
			final String EXPECTED_MSG = "正常に更新されました。";

			BookManagementScreenForm form = new BookManagementScreenForm();
			form.setBookId(5);
			form.setBookName("test");
			form.setStock(100);
			com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();

			try {
				mockMvc.perform(post("/manage/book").param("update", "").content(mapper.writeValueAsString(form))
						.contentType(MediaType.APPLICATION_JSON_VALUE))
						.andExpect(status().isOk())
						.andExpect(view().name(is("bookmanager")))
						.andExpect(model().attribute("message", EXPECTED_MSG));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Test
		void 書籍在庫情報リスト取得失敗() {
			final String EXPECTED_MSG = "更新に失敗しました。";

			BookManagementScreenForm form = new BookManagementScreenForm();
			form.setBookId(5);
			form.setBookName("test");
			form.setStock(100);
			ObjectMapper mapper = new ObjectMapper();

			Whitebox.setInternalState(sut, BookManagementService.class, bookManagementService);
			doReturn(1).when(bookManagementService).update(anyInt(), anyString(), anyInt());

			try {
				mockMvc.perform(post("/manage/book").param("update", "").content(mapper.writeValueAsString(form))
						.contentType(MediaType.APPLICATION_JSON_VALUE))
						.andExpect(status().isOk())
						.andExpect(view().name(is("bookmanager")))
						.andExpect(model().attribute("message", EXPECTED_MSG));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Nested
	class Order {
		@Test
		@Sql(statements = {
				"DELETE BOOK_MANAGEMENT_TBL",
		})
		void 書籍在庫情報リスト更新成功() {
			final String EXPECTED_MSG = "正常に発注されました。";

			BookManagementScreenForm form = new BookManagementScreenForm();
			form.setBookId(5);
			form.setBookName("test");
			form.setStock(100);
			ObjectMapper mapper = new ObjectMapper();

			doReturn(0).when(bookManagementService).order(anyInt(), anyString(), anyInt());

			try {
				mockMvc.perform(post("/manage/book").param("order", "").content(mapper.writeValueAsString(form))
						.contentType(MediaType.APPLICATION_JSON_VALUE))
						.andExpect(status().isOk())
						.andExpect(view().name(is("bookmanager")))
						.andExpect(model().attribute("message", EXPECTED_MSG));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Test
		void 書籍在庫情報リスト取得失敗() {
			final String EXPECTED_MSG = "発注に失敗しました。";

			BookManagementScreenForm form = new BookManagementScreenForm();
			form.setBookId(5);
			form.setBookName("test");
			form.setStock(100);
			ObjectMapper mapper = new ObjectMapper();

			Whitebox.setInternalState(sut, BookManagementService.class, bookManagementService);
			doReturn(1).when(bookManagementService).order(anyInt(), anyString(), anyInt());

			try {
				mockMvc.perform(post("/manage/book").param("order", "").content(mapper.writeValueAsString(form))
						.contentType(MediaType.APPLICATION_JSON_VALUE))
						.andExpect(status().isOk())
						.andExpect(view().name(is("bookmanager")))
						.andExpect(model().attribute("message", EXPECTED_MSG));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
