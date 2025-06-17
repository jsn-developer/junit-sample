package jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.dto.BookManagementTableDto;


/**
 * BookManagementTableRepositoryのテスト
 * DBアクセスを行う（実際のDBを使用）
 *
 * @author JSN
 */
@DataJpaTest
class BookManagementTableRepositoryTest {

	@Autowired
	private BookManagementTableRepository sut;

	// テストデータ生成用のヘルパーメソッド
	private static BookManagementTableDto createBook(int id, String name, int stock, int version) {
		BookManagementTableDto dto = new BookManagementTableDto();
		dto.setBookId(id);
		dto.setBookName(name);
		dto.setStock(stock);
		dto.setVersion(version);
		return dto;
	}

	private static final BookManagementTableDto SPRING_BOOT_BOOK = createBook(1, "Spring boot実践入門", 10, 1);

	private static final BookManagementTableDto JUNIT_BOOK = createBook(2, "JUnit詳解", 200, 3);

	@Nested
	class FindAll {

		@Test
		@DisplayName("全件取得成功_2件")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)",
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, 'JUnit詳解', 200, 3)"
		})
		void findAllSuccess2Records() {
			var actual = sut.findAll();

			assertThat(actual).hasSize(2);
		}

		@Test
		@DisplayName("全件取得成功_1件")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)"
		})
		void findAllSuccess1Record() {
			var actual = sut.findAll();

			assertThat(actual).hasSize(1);
		}

		@Test
		@DisplayName("全件取得成功_0件")
		void findAllSuccess0Records() {
			var actual = sut.findAll();

			assertThat(actual).hasSize(0);
		}

		@Test
		@DisplayName("全件取得成功_データ確認")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)",
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, 'JUnit詳解', 200, 3)"
		})
		void findAllDataVerification() {
			var actual = sut.findAll();

			assertThat(actual.get(0).getBookName()).isEqualTo(SPRING_BOOT_BOOK.getBookName());
			assertThat(actual.get(0).getStock()).isEqualTo(SPRING_BOOT_BOOK.getStock());
			assertThat(actual.get(0).getVersion()).isEqualTo(SPRING_BOOT_BOOK.getVersion());

			assertThat(actual.get(1).getBookName()).isEqualTo(JUNIT_BOOK.getBookName());
			assertThat(actual.get(1).getStock()).isEqualTo(JUNIT_BOOK.getStock());
			assertThat(actual.get(1).getVersion()).isEqualTo(JUNIT_BOOK.getVersion());
		}
	}

	@Nested
	class FindById {

		@Test
		@DisplayName("ID指定取得成功")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)",
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, 'JUnit詳解', 200, 3)"
		})
		void findByIdSuccess() {
			var actual = sut.findById(1);

			assertThat(actual).isPresent();
			assertThat(actual.get().getBookName()).isEqualTo(SPRING_BOOT_BOOK.getBookName());
			assertThat(actual.get().getStock()).isEqualTo(SPRING_BOOT_BOOK.getStock());
			assertThat(actual.get().getVersion()).isEqualTo(SPRING_BOOT_BOOK.getVersion());
		}

		@Test
		@DisplayName("ID指定取得_存在しないID")
		void findByIdNotFound() {
			var actual = sut.findById(999);
			assertThat(actual).isEmpty();
		}
	}

	@Nested
	class SaveAndFlush {

		@Test
		@DisplayName("新規保存成功")
		void saveNewRecordSuccess() {
			BookManagementTableDto dto = new BookManagementTableDto();
			dto.setBookName("新しい書籍");
			dto.setStock(50);
			dto.setVersion(1);

			var actual = sut.saveAndFlush(dto);

			assertThat(actual.getBookId()).isNotNull();
			assertThat(actual.getBookName()).isEqualTo("新しい書籍");
			assertThat(actual.getStock()).isEqualTo(50);
			assertThat(actual.getVersion()).isEqualTo(1);
		}

		@Test
		@DisplayName("更新成功")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, '元の書籍名', 10, 1)"
		})
		void updateRecordSuccess() {
			var existingDto = sut.findById(1).get();
			existingDto.setBookName("更新後の書籍名");
			existingDto.setStock(20);

			var actual = sut.saveAndFlush(existingDto);

			assertThat(actual.getBookId()).isEqualTo(1);
			assertThat(actual.getBookName()).isEqualTo("更新後の書籍名");
			assertThat(actual.getStock()).isEqualTo(20);
			assertThat(actual.getVersion()).isEqualTo(2);
		}
	}

	@Nested
	class Delete {

		@Test
		@DisplayName("削除成功")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, '削除対象書籍', 10, 1)"
		})
		void deleteRecordSuccess() {
			var targetDto = sut.findById(1).get();

			sut.delete(targetDto);

			var deleted = sut.findById(1);
			assertThat(deleted).isEmpty();

			var allBooks = sut.findAll();
			assertThat(allBooks).hasSize(0);
		}

		@Test
		@DisplayName("ID指定削除成功")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, '削除対象書籍', 10, 1)",
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, '残す書籍', 20, 1)"
		})
		void deleteByIdSuccess() {
			sut.deleteById(1);

			var deleted = sut.findById(1);
			assertThat(deleted).isEmpty();
		}
	}

	@Nested
	class Count {

		@Test
		@DisplayName("カウント取得成功_2件")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)",
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, 'JUnit詳解', 200, 3)"
		})
		void countRecords2() {
			var actual = sut.count();
			assertThat(actual).isEqualTo(2);
		}

		@Test
		@DisplayName("カウント取得成功_0件")
		void countRecords0() {
			var actual = sut.count();
			assertThat(actual).isEqualTo(0);
		}
	}

	@Nested
	class ExistsById {

		@Test
		@DisplayName("存在確認_存在する")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)"
		})
		void existsByIdTrue() {
			var actual = sut.existsById(1);
			assertThat(actual).isTrue();
		}

		@Test
		@DisplayName("存在確認_存在しない")
		void existsByIdFalse() {
			var actual = sut.existsById(999);
			assertThat(actual).isFalse();
		}
	}
}
