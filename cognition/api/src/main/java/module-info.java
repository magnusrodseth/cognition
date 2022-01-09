module cognition.api {
  requires spring.web;
  requires spring.boot;
  requires spring.boot.autoconfigure;

  requires cognition.core;
  requires spring.data.rest.core;
  requires spring.data.commons;
  requires spring.context;

  exports api;
  opens api;
  requires java.net.http;
  requires spring.beans;
}