module cognition.core {
  requires transitive com.google.gson;

  exports core;
  exports core.tools;
  exports json;

  opens core;
  opens core.tools;
  opens json;
}