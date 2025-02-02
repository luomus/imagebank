CREATE TABLE preferences (
  userid varchar2(10),
  preference varchar2(20),
  value varchar2(4000),
  CONSTRAINT preferences_pk PRIMARY KEY (userid, preference)
);

