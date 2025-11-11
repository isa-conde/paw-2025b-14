set database sql syntax pgs true;
set ignorecase true;

insert into users (id, email, username, password, verified, locale)
    values (100, 'another@mail.com','janedoe','1234567890',false,'es');
