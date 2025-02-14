CREATE TABLE preferences (
  userid varchar2(10),
  preference varchar2(200),
  value varchar2(4000),
  CONSTRAINT preferences_pk PRIMARY KEY (userid, preference)
);

