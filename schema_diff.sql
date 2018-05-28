CREATE SEQUENCE IF NOT EXISTS xmlerror_ids;
CREATE TABLE IF NOT EXISTS XmlError
(
	id INTEGER PRIMARY KEY DEFAULT NEXTVAL('xmlerror_ids'),
	version INTEGER,
	xpath VARCHAR(1024),
	parent_xpath VARCHAR(1024),
	line_number INTEGER,
	column_number INTEGER,
	message VARCHAR(1024),
	error_type VARCHAR(64),
	source VARCHAR(1024),
	error_dt TIMESTAMP
)