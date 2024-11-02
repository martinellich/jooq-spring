package ch.martinelli.oss.jooqspring;

import org.jooq.Record;
import org.jooq.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.row;

/**
 * Abstract repository class for CRUD operations using jOOQ.
 *
 * @param <T>  the type of the jOOQ Table
 * @param <R>  the type of the jOOQ UpdatableRecord
 * @param <ID> the type of the primary key
 */
@Transactional(readOnly = true)
public abstract class JooqRepository<T extends Table<R>, R extends UpdatableRecord<R>, ID> {

    /**
     * The DSLContext instance used for executing SQL queries and interacting with the database.
     * It serves as the primary interface for jOOQ operations.
     */
    protected final DSLContext dslContext;
    /**
     * The database table associated with this repository.
     * Represents the specific table in the database that this repository interacts with.
     * Used for performing CRUD operations.
     */
    protected final T table;

    /**
     * Constructs a new JooqRepository with the specified DSLContext and table.
     *
     * @param dslContext the DSLContext to be used for database operations
     * @param table      the table associated with this repository
     */
    public JooqRepository(DSLContext dslContext, T table) {
        this.dslContext = dslContext;
        this.table = table;
    }

    /**
     * Finds a record by its primary key.
     *
     * @param id the primary key value of the record to find
     * @return an Optional containing the found record, or empty if no record was found
     * @throws IllegalArgumentException if the table does not have a primary key
     */
    public Optional<R> findById(ID id) {
        if (table.getPrimaryKey() == null) {
            throw new IllegalArgumentException("This method can only be called on tables with a primary key");
        }
        return dslContext
                .selectFrom(table)
                .where(eq(table.getPrimaryKey(), id))
                .fetchOptional();
    }

    /**
     * Retrieves a list of records from the database with pagination and sorting.
     *
     * @param offset  the starting position of the first record
     * @param limit   the maximum number of records to retrieve
     * @param orderBy the list of fields to order the result set by
     * @return a List containing the fetched records
     */
    public List<R> findAll(int offset, int limit, List<OrderField<?>> orderBy) {
        return dslContext
                .selectFrom(table)
                .orderBy(orderBy)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * Retrieves a list of records from the database with filtering, pagination, and sorting.
     *
     * @param condition the condition to filter the records by
     * @param offset the starting position of the first record
     * @param limit the maximum number of records to retrieve
     * @param orderBy the list of fields to order the result set by
     * @return a List containing the fetched records
     */
    public List<R> findAll(Condition condition, int offset, int limit, List<OrderField<?>> orderBy) {
        return dslContext
                .selectFrom(table)
                .where(condition)
                .orderBy(orderBy)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * Counts the total number of records in the associated table.
     *
     * @return the total number of records in the table
     */
    public int count() {
        return dslContext.fetchCount(table);
    }

    /**
     * Counts the number of records in the associated table that match the given condition.
     *
     * @param condition the condition to filter the records by
     * @return the number of matching records
     */
    public int count(Condition condition) {
        return dslContext.fetchCount(table, condition);
    }

    /**
     * Saves the given record to the database. Attaches the record to the
     * DSLContext and stores it.
     *
     * @param record the record to save
     * @return the number of affected rows
     */
    @Transactional
    public int save(R record) {
        dslContext.attach(record);
        return record.store();
    }

    /**
     * Merges the given record into the database. Attaches the record to the DSLContext
     * and attempts to merge it.
     *
     * @param record the record to merge
     * @return the number of affected rows
     */
    @Transactional
    public int merge(R record) {
        dslContext.attach(record);
        return record.merge();
    }

    /**
     * Deletes the specified record from the database.
     *
     * @param record the record to delete
     * @return the number of affected rows
     */
    @Transactional
    public int delete(R record) {
        dslContext.attach(record);
        return record.delete();
    }

    /**
     * Deletes a record from the database identified by its primary key.
     *
     * @param id the primary key value of the record to delete
     * @return the number of affected rows
     * @throws IllegalArgumentException if the table does not have a primary key
     */
    @Transactional
    public int deleteById(ID id) {
        if (table.getPrimaryKey() == null) {
            throw new IllegalArgumentException("This method can only be called on tables with a primary key");
        }
        return dslContext.deleteFrom(table)
                .where(eq(table.getPrimaryKey(), id))
                .execute();
    }

    /**
     * Deletes records from the database that match the given condition.
     *
     * @param condition the condition to filter which records to delete
     * @return the number of affected rows
     */
    @Transactional
    public int delete(Condition condition) {
        return dslContext
                .deleteFrom(table)
                .where(condition).execute();
    }

    /**
     * Generates a condition to be used in database queries to match a given primary key and its value.
     * Inspired by {@link org.jooq.impl.DAOImpl}
     *
     * @param pk the primary key of the table
     * @param id the value of the primary key to match
     * @return a Condition used to match the specified primary key and its value
     */
    @SuppressWarnings("unchecked")
    private Condition eq(UniqueKey<R> pk, ID id) {
        List<TableField<R, ?>> fields = pk.getFields();
        if (fields.size() == 1) {
            TableField<R, ?> first = fields.get(0);
            return ((Field<Object>) first).equal(first.getDataType().convert(id));
        } else {
            return row(pk.getFieldsArray()).equal((Record) id);
        }
    }

}
