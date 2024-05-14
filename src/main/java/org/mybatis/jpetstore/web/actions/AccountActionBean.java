/*
 *    Copyright 2010-2024 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.jpetstore.web.actions;

import java.util.*;

import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SessionScope;
import net.sourceforge.stripes.integration.spring.SpringBean;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.AccountCreatedEvent;
import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.domain.Product;
import org.mybatis.jpetstore.repository.EventSourcedAccountRepository;
import org.mybatis.jpetstore.service.AccountService;
import org.mybatis.jpetstore.service.CatalogService;

/**
 * The Class AccountActionBean.
 *
 * @author Eduardo Macarron
 */
@SessionScope
public class AccountActionBean extends AbstractActionBean {

  private static final long serialVersionUID = 5499663666155758178L;

  private static final String NEW_ACCOUNT = "/WEB-INF/jsp/account/NewAccountForm.jsp";
  private static final String EDIT_ACCOUNT = "/WEB-INF/jsp/account/EditAccountForm.jsp";
  private static final String SIGNON = "/WEB-INF/jsp/account/SignonForm.jsp";

  private static final List<String> LANGUAGE_LIST;
  private static final List<String> CATEGORY_LIST;

  @SpringBean
  private transient AccountService accountService;
  @SpringBean
  private transient CatalogService catalogService;

  private final static EventStore eventStore = new EventStore(
      "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");

  private final static EventSourcedAccountRepository repository = new EventSourcedAccountRepository(eventStore);

  private Account account = new Account();
  private List<Product> myList;
  private boolean authenticated;

  static {
    LANGUAGE_LIST = Collections.unmodifiableList(Arrays.asList("english", "japanese"));
    CATEGORY_LIST = Collections.unmodifiableList(Arrays.asList("FISH", "DOGS", "REPTILES", "CATS", "BIRDS"));
  }

  public Account getAccount() {
    return this.account;
  }

  public String getUsername() {
    return account.getUsername();
  }

  // @Validate(required = true, on = { "signon", "newAccount", "editAccount" })
  public void setUsername(String username) {
    account.setUsername(username);
  }

  public String getPassword() {
    return account.getPassword();
  }

  public String getRepeatedPassword() {
    return account.getRepeatedPassword();
  }

  public void setRepeatedPassword(String repeatedPassword) {
    account.setRepeatedPassword(repeatedPassword);
  }

  // @Validate(required = true, on = { "signon", "newAccount", "editAccount" })
  public void setPassword(String password) {
    account.setPassword(password);
  }

  public List<Product> getMyList() {
    return myList;
  }

  public void setMyList(List<Product> myList) {
    this.myList = myList;
  }

  public List<String> getLanguages() {
    return LANGUAGE_LIST;
  }

  public List<String> getCategories() {
    return CATEGORY_LIST;
  }

  public Resolution newAccountForm() {
    return new ForwardResolution(NEW_ACCOUNT);
  }

  /**
   * New account.
   *
   * @return the resolution
   */
  public Resolution newAccount() {
    // accountService.insertAccount(account);
    // account = accountService.getAccount(account.getUsername());
    Account newAccount = new Account(account.getUsername(), account.getPassword(), account.getRepeatedPassword(),
        account.getEmail(), account.getFirstName(), account.getLastName(), account.getStatus(), account.getAddress1(),
        account.getAddress2(), account.getCity(), account.getState(), account.getZip(), account.getCountry(),
        account.getPhone(), account.getFavouriteCategoryId(), account.getLanguagePreference(), account.isListOption(),
        account.isBannerOption(), account.getBannerName());
    this.repository.save(newAccount);
    myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
    authenticated = true;
    return new RedirectResolution(CatalogActionBean.class);
  }

  /**
   * Edits the account form.
   *
   * @return the resolution
   */
  public Resolution editAccountForm() {
    return new ForwardResolution(EDIT_ACCOUNT);
  }

  /**
   * Edits the account.
   *
   * @return the resolution
   */
  public Resolution editAccount() {
    // accountService.updateAccount(account);
    // account = accountService.getAccount(account.getUsername());
    // myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());

    Account queryAccount = new Account(account.getAccountId(), account.getPassword(), account.getRepeatedPassword(),
        account.getEmail(), account.getFirstName(), account.getLastName(), account.getStatus(), account.getAddress1(),
        account.getAddress2(), account.getCity(), account.getState(), account.getZip(), account.getCountry(),
        account.getPhone(), account.getFavouriteCategoryId(), account.getLanguagePreference(), account.isListOption(),
        account.isBannerOption(), account.getBannerName());
    repository.save(queryAccount);
    myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
    return new RedirectResolution(CatalogActionBean.class);
  }

  /**
   * Signon form.
   *
   * @return the resolution
   */
  @DefaultHandler
  public Resolution signonForm() {
    return new ForwardResolution(SIGNON);
  }

  /**
   * Signon.
   *
   * @return the resolution
   */
  // public Resolution signon() {

  // account = accountService.getAccount(getUsername(), getPassword());

  // if (account == null) {
  // String value = "Invalid username or password. Signon failed.";
  // setMessage(value);
  // clear();
  // return new ForwardResolution(SIGNON);
  // } else {
  // account.setPassword(null);
  // myList = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
  // authenticated = true;
  // HttpSession s = context.getRequest().getSession();
  // // this bean is already registered as /actions/Account.action
  // s.setAttribute("accountBean", this);
  // return new RedirectResolution(CatalogActionBean.class);
  // }
  // }

  public Resolution signon() {
    List<AccountCreatedEvent> accountEvents = repository.getAccountEvents();

    AccountCreatedEvent matchedEvent = accountEvents.stream().filter(
        event -> event.getUsername().equals(account.getUsername()) && event.getPassword().equals(account.getPassword()))
        .findFirst().orElse(null);
    if (matchedEvent == null) {
      String value = "Invalid username or password. Signon failed";
      setMessage(value);
      clear();
      return new ForwardResolution(SIGNON);
    } else {
      Account queryAcount = new Account(matchedEvent.getAccountId());
      account = queryAcount;
      account.setPassword(null);
      account.setUsername(matchedEvent.getUsername());
      myList = catalogService.getProductListByCategory(matchedEvent.getFavouriteCategoryId());
      authenticated = true;
      HttpSession s = context.getRequest().getSession();
      // this bean is already registered as /actions/Account.action
      s.setAttribute("accountBean", this);
      return new RedirectResolution(CatalogActionBean.class);
    }
  }

  /**
   * Signoff.
   *
   * @return the resolution
   */
  public Resolution signoff() {
    context.getRequest().getSession().invalidate();
    clear();
    return new RedirectResolution(CatalogActionBean.class);
  }

  /**
   * Checks if is authenticated.
   *
   * @return true, if is authenticated
   */
  public boolean isAuthenticated() {
    return authenticated && account != null && account.getUsername() != null;
  }

  /**
   * Clear.
   */
  public void clear() {
    account = new Account();
    myList = null;
    authenticated = false;
  }

}
