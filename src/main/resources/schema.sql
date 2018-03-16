create table IF NOT EXISTS count
(
   id INTEGER not null auto_increment,
   runid VARCHAR not null,
   count INTEGER(4) not null,
   primary key(id)
);