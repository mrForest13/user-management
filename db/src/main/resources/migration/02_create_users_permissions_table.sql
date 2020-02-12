create TABLE USERS_PERMISSIONS (
  USER_ID    UUID REFERENCES USERS (id) ON delete CASCADE,
  PERMISSION_ID UUID REFERENCES PERMISSIONS (ID) ON update CASCADE,
  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  CONSTRAINT USERS_PERMISSIONS_PKEY PRIMARY KEY (USER_ID, PERMISSION_ID)
);