CREATE TABLE UserTable (
    user_id VARCHAR2(8) PRIMARY KEY,
    name VARCHAR2(20) NOT NULL,
    email VARCHAR2(50) UNIQUE,
    password VARCHAR2(64) NOT NULL,
    join_date DATE DEFAULT SYSDATE,
    user_level CHAR(1) DEFAULT 'N' CHECK (user_level IN ('N', 'A'))
);

CREATE TABLE Distributor (
    distributor_id VARCHAR2(8) PRIMARY KEY,
    company VARCHAR2(20) NOT NULL,
    contact VARCHAR2(15),
    reg_date DATE DEFAULT SYSDATE
);

CREATE TABLE Store (
    app_id VARCHAR2(10) PRIMARY KEY,
    app_name VARCHAR2(50) NOT NULL,
    category VARCHAR2(20) NOT NULL,
    version VARCHAR2(10) DEFAULT '1.0',
    description VARCHAR2(200),
    price NUMBER(8) DEFAULT 0,
    distributor_id VARCHAR2(8),
    release_date DATE DEFAULT SYSDATE,
    rating NUMBER(2,1) CHECK (rating >= 0 AND rating <= 5),
    CONSTRAINT fk_distributor FOREIGN KEY (distributor_id) REFERENCES Distributor(distributor_id)
);

CREATE TABLE Review (
    review_id NUMBER(10) PRIMARY KEY,
    user_id VARCHAR2(8),
    app_id VARCHAR2(10),
    rating NUMBER(2,1) NOT NULL CHECK (rating >= 0 AND rating <= 5),
    content VARCHAR2(500) NOT NULL,
    review_date DATE DEFAULT SYSDATE,
    like_count NUMBER(5) DEFAULT 0,
    report_count NUMBER(5) DEFAULT 0,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES Usertable(user_id),
    CONSTRAINT fk_app FOREIGN KEY (app_id) REFERENCES Store(app_id)
);
CREATE SEQUENCE review_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;


CREATE TABLE Purchase (
    purchase_id   NUMBER(10) PRIMARY KEY,
    user_id       VARCHAR2(8) NOT NULL,
    app_id        VARCHAR2(10) NOT NULL,
    purchase_date DATE DEFAULT SYSDATE,
    payment       NUMBER(10) NOT NULL,
    CONSTRAINT fk_purchase_user FOREIGN KEY (user_id) REFERENCES Usertable(user_id),
    CONSTRAINT fk_purchase_app  FOREIGN KEY (app_id) REFERENCES Store(app_id)
);
CREATE SEQUENCE purchase_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;



commit;
-------------------------------------------------------------------------------
select * from store;

SELECT app_id, app_name, category, price, rating FROM Store order by rating desc NULLS LAST;



ALTER TABLE PURCHASE
ADD CONSTRAINT FK_PURCHASE_APP
FOREIGN KEY (app_id)
REFERENCES STORE(app_id)
ON DELETE CASCADE;

ALTER TABLE REVIEW
ADD CONSTRAINT FK_REVIEW_APP
FOREIGN KEY (app_id)
REFERENCES STORE(app_id)
ON DELETE CASCADE;


INSERT INTO usertable (user_id, name, email, password, join_date, user_level)
VALUES ('ab12', '김철수', 'chulsoo@email.com', '1234', SYSDATE, 'N');


INSERT INTO usertable (user_id, name, email, password, join_date, user_level)
VALUES ('ab34', '박영희', 'younghee@email.com', 'abcd', SYSDATE, 'N');

INSERT INTO usertable (user_id, name, email, password, join_date, user_level)
VALUES ('admin', '강경민관리자', 'gwanri@email.com', '1111', SYSDATE, 'A');

INSERT INTO Distributor (distributor_id, company,contact, reg_date)
VALUES ('gdcom', '길동컴퍼니','010-1234-5678',  SYSDATE);

INSERT INTO Distributor (distributor_id,company, contact,  reg_date)
VALUES ('mongtec', '몽룡테크', '010-2345-6789',  SYSDATE);

INSERT INTO Distributor (distributor_id,company, contact,  reg_date)
VALUES ('kgsoft', 'KGSoft', '010-2231-6619',  SYSDATE);

INSERT INTO Distributor (distributor_id,company, contact,  reg_date)
VALUES ('test', '테스트', '010-2345-6789',  SYSDATE);

INSERT INTO Store (app_id, app_name, category, version, description, price, distributor_id, release_date, rating)
VALUES ('12', '길동게임', '게임', '1.0', '재미있는 액션 게임', 1000, 'gdcom', SYSDATE, 4.5);

INSERT INTO Store (app_id, app_name, category, version, description, price, distributor_id, release_date, rating)
VALUES ('34', '몽룡메모', '유틸리티', '2.1', '간편한 메모 앱', 0, 'mongtec', SYSDATE, 4.2);

INSERT INTO Review (review_id, user_id, app_id, rating, content, review_date, like_count, report_count)
VALUES (1, 'ab12', '12', 5.0, '정말 재미있는 게임이에요!', SYSDATE, 3, 0);

INSERT INTO Review (review_id, user_id, app_id, rating, content, review_date, like_count, report_count)
VALUES (2, 'ab34', '34', 4.0, '메모가 정말 편리해요.', SYSDATE, 1, 0);

INSERT INTO Purchase (purchase_id, user_id, app_id, purchase_date, payment)
VALUES (1, 'ab12', '12', SYSDATE, 1000);

INSERT INTO Purchase (purchase_id, user_id, app_id, purchase_date, payment)
VALUES (2, 'ab34', '12', SYSDATE, 1000);

SELECT r.review_id, u.name AS user_name, r.rating, r.content, r.review_date
	        FROM Review r
	        JOIN Usertable u ON r.user_id = u.user_id
	        WHERE r.review_id = 13;


-- 참고용
CREATE OR REPLACE TRIGGER update_rating
AFTER INSERT ON REVIEW
FOR EACH ROW
BEGIN
  -- 리뷰가 추가될 때 앱의 평균 별점 갱신
  UPDATE APP
  SET avg_rating = (
    SELECT ROUND(AVG(rating),2)
    FROM REVIEW
    WHERE app_id = :NEW.app_id
  )
  WHERE app_id = :NEW.app_id;
END;
