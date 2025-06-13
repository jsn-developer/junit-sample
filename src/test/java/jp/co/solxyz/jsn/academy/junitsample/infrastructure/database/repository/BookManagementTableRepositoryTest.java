package jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.dto.BookManagementTableDto;


/**
 * BookManagementTableRepositoryのテスト
 * DBアクセスを行う（実際のDBを使用）
 *
 * @author JSN
 */
@DataJpaTest
@Sql(statements = "TRUNCATE TABLE BOOK_MANAGEMENT_TBL RESTART IDENTITY")
class BookManagementTableRepositoryTest {

	@Autowired
	private BookManagementTableRepository sut;

	// テストデータ生成用のヘルパーメソッド
	private BookManagementTableDto createBook(Integer id, String name, Integer stock, Integer version) {
		BookManagementTableDto dto = new BookManagementTableDto();
		if (id != null) {
			dto.setBookId(id);
		}
		dto.setBookName(name);
		dto.setStock(stock);
		dto.setVersion(version);
		return dto;
	}

	// 特定のよく使う本を生成するメソッド
	private BookManagementTableDto createSpringBootBook() {
		return createBook(1, "Spring boot実践入門", 10, 1);
	}

	private BookManagementTableDto createJUnitBook() {
		return createBook(2, "JUnit詳解", 200, 3);
	}

	@Nested
	class FindAll {

		@Test
		@DisplayName("全件取得成功_2件")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)",
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, 'JUnit詳解', 200, 3)"
		})
		void findAllSuccess2Records() {
			var springBootBook = createSpringBootBook();
			var junitBook = createJUnitBook();

			var actual = sut.findAll();

			assertThat(actual).hasSize(2);
			assertThat(actual.get(0).getBookName()).isEqualTo(springBootBook.getBookName());
			assertThat(actual.get(1).getBookName()).isEqualTo(junitBook.getBookName());
		}

		@Test
		@DisplayName("全件取得成功_1件")
		@Sql(statements = {
				"INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)"
		})
		void findAllSuccess1Record() {
			var springBootBook = createSpringBootBook();

			var actual = sut.findAll();

			assertThat(actual).hasSize(1);
			assertThat(actual.get(0).getBookName()).isEqualTo(springBootBook.getBookName());
			assertThat(actual.get(0).getStock()).isEqualTo(springBootBook.getStock());
		}

		@Test
		@DisplayName("全件取得成功_0件")
		void findAllSuccess0Records() {
			var actual = sut.findAll();
			assertThat(actual).hasSize(0);
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
			var springBootBook = createSpringBootBook();

			var actual = sut.findById(1);

			assertThat(actual).isPresent();
			assertThat(actual.get().getBookName()).isEqualTo(springBootBook.getBookName());
			assertThat(actual.get().getStock()).isEqualTo(springBootBook.getStock());
			assertThat(actual.get().getVersion()).isEqualTo(springBootBook.getVersion());
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

			// DBに実際に保存されているか確認
			var saved = sut.findById(actual.getBookId());
			assertThat(saved).isPresent();
			assertThat(saved.get().getBookName()).isEqualTo("新しい書籍");
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

			// DBで実際に更新されているか確認
			var updated = sut.findById(1);
			assertThat(updated).isPresent();
			assertThat(updated.get().getBookName()).isEqualTo("更新後の書籍名");
			assertThat(updated.get().getStock()).isEqualTo(20);
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

			// 他のデータは残っていることを確認
			var remaining = sut.findById(2);
			assertThat(remaining).isPresent();
			assertThat(remaining.get().getBookName()).isEqualTo("残す書籍");

			var allBooks = sut.findAll();
			assertThat(allBooks).hasSize(1);
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
