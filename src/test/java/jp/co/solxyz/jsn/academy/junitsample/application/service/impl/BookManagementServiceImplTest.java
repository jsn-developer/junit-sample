package jp.co.solxyz.jsn.academy.junitsample.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jp.co.solxyz.jsn.academy.junitsample.infrastructure.api.component.BookOrderApi;
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

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void test() {
		fail("未実装");
	}

	@Nested
	class Init {

	}

	@Nested
	class Update {

	}

	@Nested
	class Order {

	}

}