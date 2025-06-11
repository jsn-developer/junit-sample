package jp.co.solxyz.jsn.academy.junitsample.infrastructure.database.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Data;

/**
 * 書籍管理DTO
 */
@Entity
@Table(name="BookManagementTbl")
@Data
public class BookManagementTableDto {
    /** 書籍ID */
    @Id
    @Column(name="BOOK_ID", unique = true, nullable = false)
    private int bookId;

    /** 書籍名 */
    @Column(name="BOOK_NAME", nullable = false)
    private String bookName;

    /** 在庫数 */
    @Column(name="STOCK", nullable = false)
    private int stock;

    @Column(name="VERSION", nullable = false)
    @Version
    private int version;
}
