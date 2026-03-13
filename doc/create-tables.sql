CREATE TABLE preferences (
  userid varchar2(10),
  preference varchar2(200),
  value varchar2(4000),
  CONSTRAINT preferences_pk PRIMARY KEY (userid, preference)
);


CREATE TABLE modified_taxa (
    taxon_id VARCHAR(100) PRIMARY KEY,
    modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
