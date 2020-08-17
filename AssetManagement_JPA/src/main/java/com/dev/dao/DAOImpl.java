package com.dev.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.dev.beans.Asset;
import com.dev.beans.AssetAllocation;
import com.dev.beans.AssetStatus;
import com.dev.beans.Employee;
import com.dev.beans.UserMaster;
import com.dev.exceptions.AddAssetException;
import com.dev.exceptions.AddEmployeeException;
import com.dev.exceptions.AssetAllocationException;
import com.dev.exceptions.GetAssetException;
import com.dev.exceptions.RaiseAllocationException;
import com.dev.exceptions.RemoveAssetException;
import com.dev.exceptions.StatusException;
import com.dev.exceptions.UpdateAssetException;

public class DAOImpl implements DAO {

	EntityManagerFactory entityManagerFactory = null;

	public UserMaster login(Integer userid, String password) {
		UserMaster um = new UserMaster();

		EntityManager entityManager = null;
		entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
		entityManager = entityManagerFactory.createEntityManager();
		String jpql = "select usertype from UserMaster where userid=:uid and userpassword=:upwd";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("uid", userid);
		query.setParameter("upwd", password);
		String user = (String) query.getSingleResult();
		um.setUsertype(user);

		entityManager.close();
		return um;
	}

	public Asset addAsset(Asset asset) {
		EntityManager entityManager = null;

		EntityTransaction entityTransaction = null;
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();

			Asset asset1 = entityManager.find(Asset.class, asset.getAssetid());
			if (asset1 == null) {
				entityTransaction = entityManager.getTransaction();
				entityTransaction.begin();
				entityManager.persist(asset);
				entityTransaction.commit();
				return asset;
			}

			else {
				// throw new AddAssetException("asset is already present");
				throw new AddAssetException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		entityManager.close();
		return null;
	}

	public Asset removeAsset(Integer aid) {
		EntityManager entityManager = null;

		EntityTransaction entityTransaction = null;
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();
			String jpql = "Delete from Asset where assetid=:assid";
			Asset asset = entityManager.find(Asset.class, aid);
			Query query = entityManager.createQuery(jpql);
			query.setParameter("assid", aid);
			Integer i = query.executeUpdate();
			if (i > 0) {
				entityTransaction.commit();
				return asset;
			} else {

				throw new RemoveAssetException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public List<Asset> getAllAsset() {
		EntityManager entityManager = null;
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			String jpql = "from Asset";
			TypedQuery<Asset> query = entityManager.createQuery(jpql, Asset.class);
			List<Asset> list = query.getResultList();
			if (!list.isEmpty()) {

				return list;
			} else {

				throw new GetAssetException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		entityManager.close();
		return null;

	}

	public Employee addEmployee(Employee employee) {
		EntityManager entityManager = null;

		EntityTransaction entityTransaction = null;
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			Employee employee1 = entityManager.find(Employee.class, employee.getEmpno());
			if (employee1 == null) {
				String query1 = "select deptid from Department ";

				Query query = entityManager.createQuery(query1);
				List<Integer> list = (List<Integer>) query.getResultList();
				for (Integer emp : list) {

					if (employee.getDeptid() == emp) {
						entityTransaction = entityManager.getTransaction();
						entityTransaction.begin();

						entityManager.persist(employee);
						entityTransaction.commit();
						return employee;

					}
				}

				throw new AddEmployeeException();

			} else {

				throw new AddEmployeeException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public AssetAllocation raiseAllocation(AssetAllocation assetallocation) {
		EntityManager entityManager = null;

		EntityTransaction entityTransaction = null;
		try {
			AssetStatus as = new AssetStatus();
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			AssetAllocation assetallocation1 = entityManager.find(AssetAllocation.class,
					assetallocation.getAllocationid());
			if (assetallocation1 == null) {
				entityTransaction = entityManager.getTransaction();
				entityTransaction.begin();
				entityManager.persist(assetallocation);
				as.setAllocationid(assetallocation.getAllocationid());
				as.setStatus("null");
				entityManager.persist(as);

				entityTransaction.commit();
				return assetallocation;

			}

			else {

				throw new RaiseAllocationException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		entityManager.close();
		return null;
	}

	public List<AssetAllocation> getAllAssetAllocation() {
		EntityManager entityManager = null;

		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			String jpql = "from AssetAllocation";
			Query query = entityManager.createQuery(jpql);
			List<AssetAllocation> list = query.getResultList();
			if (!list.isEmpty()) {
				return list;

			} else {

				throw new AssetAllocationException();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		entityManager.close();
		return null;
	}

	public Boolean setStatus(Integer allocationid, String status) {

		EntityManager entityManager = null;

		EntityTransaction entityTransaction = null;
		try {
			// System.out.println("Enter The Status");
			// String status=sc.next();
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();
			String jpql = "UPDATE AssetStatus SET status=:asname WHERE allocationid=:allocid";

			Query query = entityManager.createQuery(jpql);
			query.setParameter("asname", status);
			query.setParameter("allocid", allocationid);
			Integer i = query.executeUpdate();
			if (i > 0) {
				entityTransaction.commit();
				return true;
			} else {

				throw new StatusException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String viewStatus(Integer allocationid) {
		EntityManager entityManager = null;

		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			String jpql = "from AssetStatus where allocationid=:allocid";
			Query query = entityManager.createQuery(jpql);
			query.setParameter("allocid", allocationid);
			List<AssetStatus> list = query.getResultList();
			for (AssetStatus astatus : list) {
				System.out.println(astatus.getAllocationid());
				if (astatus.getStatus().equals("null")) {
					return "Status not updated till now";
				} else {
					return astatus.getStatus();
				}
			}

			throw new StatusException();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return "enter valid allocation id";
	}

	public Asset updateAssetName(Integer aid, String assetname) {
		EntityManager entityManager = null;

		EntityTransaction entityTransaction = null;
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			entityTransaction = entityManager.getTransaction();
			Asset asset = entityManager.find(Asset.class, aid);
			entityTransaction.begin();
			String jpql = "UPDATE Asset SET assetname=:asname WHERE assetid=:aid";

			Query query = entityManager.createQuery(jpql);
			query.setParameter("asname", assetname);
			query.setParameter("aid", aid);
			Integer i = query.executeUpdate();
			if (i > 0) {
				asset.setAssetname(assetname);
				entityTransaction.commit();
				return asset;
			} else {
				throw new UpdateAssetException();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;

	}

	public Asset updateAssetDes(Integer aid, String assetdes) {
		EntityManager entityManager = null;

		EntityTransaction entityTransaction = null;
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			entityTransaction = entityManager.getTransaction();
			Asset asset1 = entityManager.find(Asset.class, aid);
			entityTransaction.begin();
			String jpql = "UPDATE Asset SET assetdes=:asdes WHERE assetid=:aid";

			Query query = entityManager.createQuery(jpql);
			query.setParameter("asdes", assetdes);
			query.setParameter("aid", aid);
			Integer i = query.executeUpdate();
			if (i > 0) {
				asset1.setAssetdes(assetdes);
				entityTransaction.commit();
				return asset1;
			} else {
				throw new UpdateAssetException();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public Asset updateAssetQuantity(Integer aid, Integer assetquan) {
		EntityManager entityManager = null;

		EntityTransaction entityTransaction = null;
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			entityTransaction = entityManager.getTransaction();
			Asset asset2 = entityManager.find(Asset.class, aid);
			entityTransaction.begin();
			String jpql2 = "UPDATE Asset SET quantity=:aquan WHERE assetid=:aid";

			Query query2 = entityManager.createQuery(jpql2);
			query2.setParameter("aquan", assetquan);
			query2.setParameter("aid", aid);
			Integer i2 = query2.executeUpdate();
			if (i2 > 0) {
				asset2.setQuantity(assetquan);
				entityTransaction.commit();
				return asset2;
			} else {
				throw new UpdateAssetException();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public Asset updateAssetStatus(Integer aid, String assetstatus) {
		EntityManager entityManager = null;

		EntityTransaction entityTransaction = null;
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			entityManager = entityManagerFactory.createEntityManager();
			entityTransaction = entityManager.getTransaction();
			Asset asset3 = entityManager.find(Asset.class, aid);
			entityTransaction.begin();
			String jpql3 = "UPDATE Asset SET status=:assetstatus WHERE assetid=:aid";

			Query query3 = entityManager.createQuery(jpql3);
			query3.setParameter("assetstatus", assetstatus);
			query3.setParameter("aid", aid);
			Integer i3 = query3.executeUpdate();
			if (i3 > 0) {
				asset3.setStatus(assetstatus);
				entityTransaction.commit();
				return asset3;
			} else {
				throw new UpdateAssetException();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

}
