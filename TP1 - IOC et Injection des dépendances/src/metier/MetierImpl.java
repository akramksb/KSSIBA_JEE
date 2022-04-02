package metier;

import dao.IDao;

public class MetierImpl implements IMetier{
    private IDao dao;
    @Override
    public double calcul() {
        return 2 * dao.getValue();
    }

    public void setDao(IDao dao) {
        this.dao = dao;
    }
}
