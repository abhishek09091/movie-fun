package org.superbiz.moviefun.databaseconfiguration;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.superbiz.moviefun.DatabaseServiceCredentials;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


@Configuration
public class DatabaseConfiguration {

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(dataSource);
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        return hikariDataSource;
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(dataSource);
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        return hikariDataSource;
    }


    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanAlbums(HibernateJpaVendorAdapter hibernateJpaVendorAdapter,
                                                                                               DataSource albumsDataSource){

        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanAlbums = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBeanAlbums.setDataSource(albumsDataSource);
        localContainerEntityManagerFactoryBeanAlbums.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryBeanAlbums.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryBeanAlbums.setPersistenceUnitName("album");
        return localContainerEntityManagerFactoryBeanAlbums;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanMovies(HibernateJpaVendorAdapter hibernateJpaVendorAdapter,
                                                                                               DataSource moviesDataSource){
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanMovies = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBeanMovies.setDataSource(moviesDataSource);
        localContainerEntityManagerFactoryBeanMovies.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryBeanMovies.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryBeanMovies.setPersistenceUnitName("movie");
        return localContainerEntityManagerFactoryBeanMovies;
    }

    @Bean
    public DatabaseServiceCredentials databaseServiceCredentials(){
        String vcapServicesJson =  System.getenv("VCAP_SERVICES");
       // System.out.println("vcapServices: "+vcapServicesJson);
        return new DatabaseServiceCredentials(vcapServicesJson);
    }

    @Bean
    public PlatformTransactionManager platformTransactionManagerAlbum(EntityManagerFactory localContainerEntityManagerFactoryBeanAlbums){
        return new JpaTransactionManager(localContainerEntityManagerFactoryBeanAlbums);
    }

    @Bean
    public PlatformTransactionManager platformTransactionManagerMovie(EntityManagerFactory localContainerEntityManagerFactoryBeanMovies){
        return new JpaTransactionManager(localContainerEntityManagerFactoryBeanMovies);
    }

    @Bean
    public TransactionTemplate transactionTemplateAlbum(PlatformTransactionManager platformTransactionManagerAlbum){
        return new TransactionTemplate(platformTransactionManagerAlbum);
    }

    @Bean
    public TransactionTemplate transactionTemplateMovie(PlatformTransactionManager platformTransactionManagerMovie){
        return new TransactionTemplate(platformTransactionManagerMovie);
    }

}
