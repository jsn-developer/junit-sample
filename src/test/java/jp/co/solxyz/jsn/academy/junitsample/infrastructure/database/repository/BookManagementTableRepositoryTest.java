package jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.dto.BookManagementTableDto;


/**
 * BookManagementTableRepositoryのテスト
 * DBアクセスを行う（実際のDBを使用）
 *
 * @author JSN
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
class BookManagementTableRepositoryTest {

  @Autowired
  private BookManagementTableRepository sut;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Nested
  class FindAll {

    @Test
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, 'JUnit詳解', 200, 3)"
    })
    void 全件取得成功_2件() {
      var actual = sut.findAll();
      assertThat(actual).hasSize(2);
      assertThat(actual.get(0).getBookName()).isEqualTo("Spring boot実践入門");
      assertThat(actual.get(1).getBookName()).isEqualTo("JUnit詳解");
    }

    @Test
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)"
    })
    void 全件取得成功_1件() {
      var actual = sut.findAll();
      assertThat(actual).hasSize(1);
      assertThat(actual.get(0).getBookName()).isEqualTo("Spring boot実践入門");
      assertThat(actual.get(0).getStock()).isEqualTo(10);
    }

    @Test
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL"
    })
    void 全件取得成功_0件() {
      var actual = sut.findAll();
      assertThat(actual).hasSize(0);
    }
  }

  @Nested
  class FindById {

    @Test
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, 'JUnit詳解', 200, 3)"
    })
    void ID指定取得成功() {
      var actual = sut.findById(1);
      assertThat(actual).isPresent();
      assertThat(actual.get().getBookName()).isEqualTo("Spring boot実践入門");
      assertThat(actual.get().getStock()).isEqualTo(10);
      assertThat(actual.get().getVersion()).isEqualTo(1);
    }

    @Test
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)"
    })
    void ID指定取得_存在しないID() {
      var actual = sut.findById(999);
      assertThat(actual).isEmpty();
    }
  }

  @Nested
  class SaveAndFlush {

    @Test
    void 新規保存成功() {
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
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, '元の書籍名', 10, 1)"
    })
    void 更新成功() {
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
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, '削除対象書籍', 10, 1)"
    })
    void 削除成功() {
      var targetDto = sut.findById(1).get();

      sut.delete(targetDto);

      var deleted = sut.findById(1);
      assertThat(deleted).isEmpty();

      var allBooks = sut.findAll();
      assertThat(allBooks).hasSize(0);
    }

    @Test
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, '削除対象書籍', 10, 1)",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, '残す書籍', 20, 1)"
    })
    void ID指定削除成功() {
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
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (2, 'JUnit詳解', 200, 3)"
    })
    void カウント取得成功_2件() {
      var actual = sut.count();
      assertThat(actual).isEqualTo(2);
    }

    @Test
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL"
    })
    void カウント取得成功_0件() {
      var actual = sut.count();
      assertThat(actual).isEqualTo(0);
    }
  }

  @Nested
  class ExistsById {

    @Test
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL",
        "INSERT INTO BOOK_MANAGEMENT_TBL (BOOK_ID, BOOK_NAME, STOCK, VERSION) VALUES (1, 'Spring boot実践入門', 10, 1)"
    })
    void 存在確認_存在する() {
      var actual = sut.existsById(1);
      assertThat(actual).isTrue();
    }

    @Test
    @Sql(statements = {
        "DELETE BOOK_MANAGEMENT_TBL"
    })
    void 存在確認_存在しない() {
      var actual = sut.existsById(999);
      assertThat(actual).isFalse();
    }
  }
}
