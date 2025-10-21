package com.example.bloggingapp.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({AuthTests.class, UserTests.class, PostTests.class, CommentTests.class, AdminTests.class})
public class TestSuite {
}
