--
-- PostgreSQL database dump
--

-- Dumped from database version 16.2
-- Dumped by pg_dump version 17.4

-- Started on 2026-05-16 18:09:13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 226 (class 1259 OID 25580)
-- Name: address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.address (
    id integer NOT NULL,
    street_id integer,
    bldng_id integer
);


ALTER TABLE public.address OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 25579)
-- Name: address_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.address ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 228 (class 1259 OID 25586)
-- Name: bldng; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bldng (
    id integer NOT NULL,
    number character varying,
    dscrptn character varying
);


ALTER TABLE public.bldng OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 25585)
-- Name: bldng_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.bldng ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.bldng_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 224 (class 1259 OID 25555)
-- Name: events; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.events (
    id integer NOT NULL,
    name character varying,
    cost numeric
);


ALTER TABLE public.events OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 25554)
-- Name: events_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.events ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.events_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 220 (class 1259 OID 25536)
-- Name: household; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.household (
    id integer NOT NULL,
    master_inh_id integer,
    addrss_id integer
);


ALTER TABLE public.household OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 25535)
-- Name: household_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.household ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.household_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 218 (class 1259 OID 25525)
-- Name: inhabitant; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inhabitant (
    id integer NOT NULL,
    name character varying,
    phone character varying,
    hh_id integer
);


ALTER TABLE public.inhabitant OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 25524)
-- Name: inhabitant_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.inhabitant ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.inhabitant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 222 (class 1259 OID 25544)
-- Name: payment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payment (
    id integer NOT NULL,
    hh_id integer,
    paydate date,
    evnt_id integer
);


ALTER TABLE public.payment OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 25543)
-- Name: payment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.payment ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.payment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 216 (class 1259 OID 25517)
-- Name: street; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.street (
    id integer NOT NULL,
    name character varying
);


ALTER TABLE public.street OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 25516)
-- Name: street_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.street ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.street_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 4835 (class 0 OID 25580)
-- Dependencies: 226
-- Data for Name: address; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (1, 2, 1);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (2, 2, 2);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (3, 2, 3);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (4, 2, 4);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (5, 1, 5);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (6, 1, 6);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (7, 1, 7);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (8, 1, 8);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (9, 3, 9);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (10, 3, 10);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (11, 3, 11);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (12, 3, 12);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (13, 4, 13);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (14, 4, 14);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (15, 4, 15);
INSERT INTO public.address OVERRIDING SYSTEM VALUE VALUES (16, 4, 16);


--
-- TOC entry 4837 (class 0 OID 25586)
-- Dependencies: 228
-- Data for Name: bldng; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (1, '316', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (2, '312', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (3, '313', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (4, '315', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (5, '363', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (6, '365', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (7, '368', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (8, '370', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (9, '385', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (10, '376', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (11, '370', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (12, '412', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (13, '27', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (14, '517', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (15, '520', '');
INSERT INTO public.bldng OVERRIDING SYSTEM VALUE VALUES (16, '416', '');


--
-- TOC entry 4833 (class 0 OID 25555)
-- Dependencies: 224
-- Data for Name: events; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.events OVERRIDING SYSTEM VALUE VALUES (1, '2026 февраль', 400);
INSERT INTO public.events OVERRIDING SYSTEM VALUE VALUES (2, '2026 март', 500);


--
-- TOC entry 4829 (class 0 OID 25536)
-- Dependencies: 220
-- Data for Name: household; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (1, 1, 1);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (2, 2, 2);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (3, 3, 3);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (4, 4, 4);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (5, 5, 5);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (6, 6, 6);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (7, 7, 7);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (9, 15, 8);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (10, 18, 9);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (11, 20, 10);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (12, 21, 11);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (13, 22, 12);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (14, 24, 13);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (15, 25, 14);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (16, 28, 15);
INSERT INTO public.household OVERRIDING SYSTEM VALUE VALUES (17, 29, 16);


--
-- TOC entry 4827 (class 0 OID 25525)
-- Dependencies: 218
-- Data for Name: inhabitant; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (8, 'Анна', '88-88-88', 1);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (9, 'Наталья', '99-99-99', 2);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (1, 'Иван', '11-11-11', 1);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (2, 'Пётр', '22-22-22', 2);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (3, 'Сергей', '33-33-33', 3);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (4, 'Николай', '44-44-44', 4);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (5, 'Михаил', '55-55-55', 5);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (6, 'Дмитрий', '66-66-66', 6);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (7, 'Сергей', '77-77-77', 7);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (10, 'Елена', '10-10-10', 3);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (11, 'Ольга', '111-111-111', 4);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (12, 'Марина', '112-112-112', 5);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (13, 'Вера', '113-113-113', 6);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (14, 'Светлана', '114-114-114', 7);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (15, 'Пётр', '12-56-87', 9);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (16, 'Инга', '63-89-24', 9);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (17, 'Вячеслав', '12-56-87', 10);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (18, 'Евдокия', '63-89-24', 10);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (19, 'Павел', '12-56-87', 11);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (20, 'Екатерина', '63-89-24', 11);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (21, 'Пётр', '12-56-87', 12);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (22, 'Ольга', '63-89-24', 13);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (23, 'Артур', '12-56-87', 14);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (24, 'Зульфия', '63-89-24', 14);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (25, 'Роман', '12-56-87', 15);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (26, 'Сельма', '63-89-24', 15);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (27, 'Максим', '12-56-87', 16);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (28, 'Анна', '63-89-24', 16);
INSERT INTO public.inhabitant OVERRIDING SYSTEM VALUE VALUES (29, 'Виктория', '12-56-87', 17);


--
-- TOC entry 4831 (class 0 OID 25544)
-- Dependencies: 222
-- Data for Name: payment; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.payment OVERRIDING SYSTEM VALUE VALUES (1, 1, 1, NULL, 1);
INSERT INTO public.payment OVERRIDING SYSTEM VALUE VALUES (2, 1, 2, NULL, 1);
INSERT INTO public.payment OVERRIDING SYSTEM VALUE VALUES (4, 2, 1, NULL, 2);
INSERT INTO public.payment OVERRIDING SYSTEM VALUE VALUES (3, 1, 4, NULL, 1);
INSERT INTO public.payment OVERRIDING SYSTEM VALUE VALUES (5, 2, 3, NULL, 2);
INSERT INTO public.payment OVERRIDING SYSTEM VALUE VALUES (6, 2, 6, NULL, 2);


--
-- TOC entry 4825 (class 0 OID 25517)
-- Dependencies: 216
-- Data for Name: street; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.street OVERRIDING SYSTEM VALUE VALUES (2, 'Зелёная');
INSERT INTO public.street OVERRIDING SYSTEM VALUE VALUES (1, 'Голубая');
INSERT INTO public.street OVERRIDING SYSTEM VALUE VALUES (3, 'Розовая');
INSERT INTO public.street OVERRIDING SYSTEM VALUE VALUES (4, 'Сиреневая');


--
-- TOC entry 4844 (class 0 OID 0)
-- Dependencies: 225
-- Name: address_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.address_id_seq', 16, true);


--
-- TOC entry 4845 (class 0 OID 0)
-- Dependencies: 227
-- Name: bldng_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bldng_id_seq', 16, true);


--
-- TOC entry 4846 (class 0 OID 0)
-- Dependencies: 223
-- Name: events_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.events_id_seq', 2, true);


--
-- TOC entry 4847 (class 0 OID 0)
-- Dependencies: 219
-- Name: household_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.household_id_seq', 17, true);


--
-- TOC entry 4848 (class 0 OID 0)
-- Dependencies: 217
-- Name: inhabitant_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.inhabitant_id_seq', 29, true);


--
-- TOC entry 4849 (class 0 OID 0)
-- Dependencies: 221
-- Name: payment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.payment_id_seq', 6, true);


--
-- TOC entry 4850 (class 0 OID 0)
-- Dependencies: 215
-- Name: street_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.street_id_seq', 4, true);


--
-- TOC entry 4675 (class 2606 OID 25584)
-- Name: address address_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pk PRIMARY KEY (id);


--
-- TOC entry 4677 (class 2606 OID 25592)
-- Name: bldng bldng_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bldng
    ADD CONSTRAINT bldng_pk PRIMARY KEY (id);


--
-- TOC entry 4673 (class 2606 OID 25561)
-- Name: events events_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pk PRIMARY KEY (id);


--
-- TOC entry 4669 (class 2606 OID 25542)
-- Name: household household_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.household
    ADD CONSTRAINT household_pk PRIMARY KEY (id);


--
-- TOC entry 4667 (class 2606 OID 25534)
-- Name: inhabitant inhabitant_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inhabitant
    ADD CONSTRAINT inhabitant_pk PRIMARY KEY (id);


--
-- TOC entry 4671 (class 2606 OID 25548)
-- Name: payment payment_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_pk PRIMARY KEY (id);


--
-- TOC entry 4665 (class 2606 OID 25523)
-- Name: street street_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.street
    ADD CONSTRAINT street_pk PRIMARY KEY (id);


--
-- TOC entry 4678 (class 2606 OID 25564)
-- Name: household household_inhabitant_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.household
    ADD CONSTRAINT household_inhabitant_fk FOREIGN KEY (inh_id) REFERENCES public.inhabitant(id);


--
-- TOC entry 4679 (class 2606 OID 25574)
-- Name: payment payment_events_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_events_fk FOREIGN KEY (evnt_id) REFERENCES public.events(id);


--
-- TOC entry 4680 (class 2606 OID 25549)
-- Name: payment payment_household_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_household_fk FOREIGN KEY (hh_id) REFERENCES public.household(id);


--
-- TOC entry 4843 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT ALL ON SCHEMA public TO postgres;


-- Completed on 2026-05-16 18:09:14

--
-- PostgreSQL database dump complete
--

